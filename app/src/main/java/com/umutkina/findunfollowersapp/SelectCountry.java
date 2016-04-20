package com.umutkina.findunfollowersapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.Location;
import twitter4j.ResponseList;


public class SelectCountry extends BaseActivity {
    ArrayList<String> countries = new ArrayList<>();
    @InjectView(R.id.tv_header)
    TextView tvHeader;
    @InjectView(R.id.lv_hashtag)
    ListView lvHashtag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        ButterKnife.inject(this);
        new GetREsponseList().execute();
    }

    class GetREsponseList extends AsyncTask<Void, ResponseList<Location>, ResponseList<Location>> {


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        /**
         * getting Places JSON
         */
        @Override
        protected ResponseList<Location> doInBackground(Void... params) {
            ResponseList<Location> locations = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);

//                Long[] ids = new Long[args[0].size()];
//                ids=args[0].toArray(ids);
                locations = twitter.getAvailableTrends();
            } catch (Exception e1) {
                e1.printStackTrace();
//                Toast.makeText(SelectCountry.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                finish();
            }
            return locations;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(final ResponseList<Location> trends) {
            // dismiss the dialog after getting all products
            final ArrayList<String> hastags = new ArrayList<>();
            for (int i = 0; i < trends.size(); i++) {
                Location location = trends.get(i);
                System.out.println(location.getCountryName() + " woeID : " + location.getWoeid());
                countries.add(location.getCountryName() + " ("+location.getName() + ")");

            }


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SelectCountry.this, android.R.layout.simple_list_item_checked, countries);
            lvHashtag.setAdapter(arrayAdapter);
            lvHashtag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSharedPreferences.edit().putLong("woeId", trends.get(position).getWoeid()).commit();
                    finish();
                }
            });

        }


        // updating UI from Background Thread
    }

}
