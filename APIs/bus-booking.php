<?php

	require_once 'KLogger.php';
	$log = new KLogger('/var/www/html/travel_mate/logs/log_bus-booking.txt',KLogger::DEBUG);
	require 'inc/connection.inc.php';
	require 'inc/function.inc.php';
	
	$redbus_url = "https://www.redbus.in/Booking/SearchResultsJSON.aspx";
	$final_response = array();
	
	if(isset($_GET['src']) && isset($_GET['dest']) && isset($_GET['date'])){
		$source_city_name = trim($_GET['src']);
		$destination_city_name = trim($_GET['dest']);
		$date_string = trim($_GET['date']);
		$log->LogInfo("source city name: ".$source_city_name);
		$log->LogInfo("destination city name: ".$destination_city_name);
		
		$cities_json = json_decode(file_get_contents('res/redbus-cities.json'), true);
		
		$soruce_redbus_id = array_search(strtolower($source_city_name),array_map('strtolower',$cities_json));
		$log->LogInfo("source redbus id: ".$soruce_redbus_id);
		$destination_redbus_id = array_search(strtolower($destination_city_name),array_map('strtolower',$cities_json));
		$log->LogInfo("destination redbus id: ".$destination_redbus_id);

		$redbus_url = $redbus_url . '?fromCityId=' . $soruce_redbus_id . '&toCityId=' . $destination_redbus_id . '&doj=' . $date_string;
		$log->LogInfo("redbus url: ".$redbus_url);
		
		$redbus_response = json_decode(curl_URL_call($redbus_url), true);
		foreach ($redbus_response as $value) {
			$log->LogInfo("response object is: ".$value);
		}
		
		
		foreach($redbus_response['data'] as $plan){
			$temp_array = array(
				'name'		=> $plan['serviceName'],
				'type'		=> $plan['BsTp'],
				'is_AC'		=> (bool)$plan['IsAc'],
				'owner'		=> $plan['Tvs'],
				'contact'	=> $plan['BPLst'][0]['BpContactNo'],
				'dep_add'	=> $plan['BPLst'][0]['BpAddress'],
				'fair'		=> $plan['FrLst'][0]
			);
			
			array_push($final_response, $temp_array);
		}
		
		$temp_array = array(
//			'name'		=> 'Suryansh Bus travels and NON-AC',
			'name'		=> 'ABC travels and NON-AC',
			'type'		=> 'Non A/C Seater/Sleeper (2+1)',
			'is_AC'		=> (bool)true,
//			'owner'		=> 'suryansh Travels',
			'owner'		=> 'ABC Travels',
//			'contact'	=> '+91 - 8860870822',
			'contact'	=> '+91 - XXXXXXXX',
			'dep_add'	=> 'Sunny kachhia, hosur road',
			'fair'		=> 2100
		);
		array_push($final_response, $temp_array);
	}
	
	echo json_encode(array('results' => $final_response));
