<?php

	require_once 'KLogger.php';
	$log = new KLogger('/var/www/html/travel_mate/logs/log_get-trains.txt',KLogger::DEBUG);
	require 'inc/connection.inc.php';
	require 'inc/function.inc.php';
	
	$train_api_code = "hkdwe9671";
	$final_response = array();
	
	if(isset($_GET['src_city']) && isset($_GET['dest_city']) && isset($_GET['date'])){
	
		$source = strtolower(trim($_GET['src_city']));
		$destination = strtolower(trim($_GET['dest_city']));
		
		$src_url = 'http://api.railwayapi.com/name_to_code/station/' . $source . '/apikey/' . $train_api_code . '/';
		$log->LogInfo("source url: ".$src_url);
 		$response = json_decode(curl_URL_call($src_url), true);
 		$src_code = end($response['stations'])['code'];
		$log->LogInfo("source code: ".$src_code);
 		
		if ($destination == "bengaluru") {
			$destination = "bangalore";
		}
		$dest_url = 'http://api.railwayapi.com/name_to_code/station/' . $destination . '/apikey/' . $train_api_code . '/';
		$log->LogInfo("dest url: ".$dest_url);
 		$response = json_decode(curl_URL_call($dest_url), true);
 		$dest_code = end($response['stations'])['code'];
		$log->LogInfo("destination code: ".$dest_code);
 		
		$trains_url = 'http://api.railwayapi.com/between/source/' . $src_code . '/dest/' . $dest_code . '/date/' . $_GET['date'] . '/apikey/' . $train_api_code . '/';
		$log->LogInfo("trains url: ".$trains_url);
		
		$response = json_decode(curl_URL_call($trains_url), true);
		foreach ($response as $value) {
			$log->LogInfo("response object is: ".$value);
		}

		foreach($response['train'] as $train){
			$temp_array = array(
				'train_number'		=> $train['number'],
				'name'			=> $train['name'],
				'departure_time'	=> $train['src_departure_time'],
				'arrival_time'		=> $train['dest_arrival_time'],
			);
			
			$temp_array['days'] = array();
			
			foreach($train['days'] as $day){
			
				if(strtolower($day['runs']) == 'y')
					array_push($temp_array['days'], 1);
				else
					array_push($temp_array['days'], 0);
			}
			
			array_push($final_response, $temp_array);
		}
	}	
	
	echo json_encode(array('trains' => $final_response));
