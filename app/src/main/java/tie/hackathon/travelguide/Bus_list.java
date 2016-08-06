package tie.hackathon.travelguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import Util.Constants;
import Util.Utils;
import adapters.Books_adapter;
import adapters.Bus_adapter;

public class Bus_list extends AppCompatActivity implements OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    ProgressBar pb;
    ListView lv;
    SharedPreferences s ;
    SharedPreferences.Editor e;
    TextView selectdate;
    TextView city;
    String source,dest;
    String dates = "17-October-2015";
    public static final String DATEPICKER_TAG = "datepicker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        s = PreferenceManager.getDefaultSharedPreferences(this);
        e = s.edit();
        source = s.getString(Constants.SOURCE_CITY, "delhi");
        dest = s.getString(Constants.DESTINATION_CITY, "mumbai");
        lv = (ListView) findViewById(R.id.music_list);
        pb = (ProgressBar) findViewById(R.id.pb);
        selectdate = (TextView) findViewById(R.id.seldate);

        city = (TextView)findViewById(R.id.city);

        selectdate.setText(dates);
        city.setText(source +" to " + dest);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Bus_list.this,SelectCity.class);
                startActivity(i);
            }
        });

        try {
            new Book_RetrieveFeed().execute();

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Can't connect.");
            alertDialog.setMessage("We cannot connect to the internet right now. Please try again later.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }


        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        setTitle("Buses");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    private boolean isVibrate() {
        return false;
    }

    private boolean isCloseOnSingleTapDay() {
        return false;
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        dates = day+"-";

        String monthString;
        switch (month+1) {
            case 1:  monthString = "January";       break;
            case 2:  monthString = "February";      break;
            case 3:  monthString = "March";         break;
            case 4:  monthString = "April";         break;
            case 5:  monthString = "May";           break;
            case 6:  monthString = "June";          break;
            case 7:  monthString = "July";          break;
            case 8:  monthString = "August";        break;
            case 9:  monthString = "September";     break;
            case 10: monthString = "October";       break;
            case 11: monthString = "November";      break;
            case 12: monthString = "December";      break;
            default: monthString = "Invalid month"; break;
        }

        dates = dates + monthString;
        dates = dates+"-"+year;

        selectdate.setText(dates);
        try {
            new Book_RetrieveFeed().execute();


        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(Bus_list.this).create();
            alertDialog.setTitle("Can't connect.");
            alertDialog.setMessage("We cannot connect to the internet right now. Please try again later. Exception e: " + e.toString());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            Log.e("YouTube:", "Cannot fetch " + e.toString());
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() ==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }

    public class Book_RetrieveFeed extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            try {
                String uri = "http://119.9.109.45/travel_mate/bus-booking.php?src=" +
                        source +
                        "&dest=" +
                        dest +
                        "&date=" +
                        dates;

                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                String readStream = Utils.readStream(con.getInputStream());

                Log.e("here",uri + readStream+" ");
                return readStream;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String Result) {
            try {
                JSONObject YTFeed = new JSONObject(String.valueOf(Result));
                JSONArray YTFeedItems = YTFeed.getJSONArray("results");
                Log.e("response", YTFeedItems + " ");
                pb.setVisibility(View.GONE);
                lv.setAdapter(new Bus_adapter(Bus_list.this, YTFeedItems));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        source = s.getString(Constants.SOURCE_CITY, "delhi");
        dest = s.getString(Constants.DESTINATION_CITY, "mumbai");
        city.setText(source +" to " + dest);

        try {
            new Book_RetrieveFeed().execute();


        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(Bus_list.this).create();
            alertDialog.setTitle("Can't connect.");
            alertDialog.setMessage("We cannot connect to the internet right now. Please try again later. Exception e: " + e.toString());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            Log.e("YouTube:", "Cannot fetch " + e.toString());
        }
    }
}
