package tie.hackathon.travelguide;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;

import adapters.Books_mainadapter;

public class Books_new extends AppCompatActivity {

    public static ViewPager pager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_new);




        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (position == 1) {

                }
            }
        });

        PagerTabStrip titleStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();
        /** Instantiating FragmentPagerAdapter */
        Books_mainadapter pageAdapter = new Books_mainadapter(fm);
        /** Setting the pagerAdapter to the pager object */
        pager.setAdapter(pageAdapter);

        setTitle("Books");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() ==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }


}
