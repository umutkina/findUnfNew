package com.umutkina.findunfollowersapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.umutkina.findunfollowersapp.adapters.ClearTweetAdapter;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class ClearTweetActivity extends BaseActivity {
    ProgressDialog pDialog;

    @InjectView(R.id.tv_header)
    TextView tvHeader;
    @InjectView(R.id.lv_tweets)
    ListView lvTweets;
    @InjectView(R.id.tv_more)
    TextView tvMore;
    @InjectView(R.id.adView)
    AdView adView;
    private Twitter twitter;
    ArrayList<Status> statuses = new ArrayList<>();
    ClearTweetAdapter clearTweetAdapter;
    Paging page;
    int pageCount = 1;
    private boolean isAllTweet = false;
    InterstitialAd mInterstitialAd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_tweet);
        ButterKnife.inject(this);
        twitter = unfApplication.getTwitter();
        isAllTweet = getIntent().getExtras().getBoolean("isAll");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        page = new Paging(1, 50);

        clearTweetAdapter = new ClearTweetAdapter(this);
        lvTweets.setAdapter(clearTweetAdapter);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page.setPage(++pageCount);
                new GetFollowersTask().execute();
                int i = Utils.randInt(3);
                if (i==2) {
                    mInterstitialAd.show();

                }

            }
        });

        new GetFollowersTask().execute();
        AdRequest adRequestInter = new AdRequest.Builder().
//                addTestDevice("2795DA65D50FE4C721767208480E9ABC").
        build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4443948134096736/4830862004");
        mInterstitialAd.loadAd(adRequestInter);
        mInterstitialAd.setAdListener(adListener);

    }

    class GetFollowersTask extends AsyncTask<Long, ResponseList<Status>, ResponseList<Status>> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ClearTweetActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected ResponseList<twitter4j.Status> doInBackground(Long... args) {
            ResponseList<twitter4j.Status> userTimeline = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);


                userTimeline = twitter.getUserTimeline(page);


            } catch (TwitterException e1) {
                e1.printStackTrace();
            }
            return userTimeline;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(ResponseList<twitter4j.Status> timeline) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            for (twitter4j.Status status : timeline) {

                statuses.add(status);
                clearTweetAdapter.setStatuses(statuses);
                clearTweetAdapter.notifyDataSetChanged();
//                try {
//                    twitter.destroyStatus(status.getId());
//                } catch (TwitterException e) {
//                    e.printStackTrace();
//                }

            }

            if (isAllTweet && timeline.size() > 0) {
                removeTweet(statuses.get(0).getId());

            }
//            else if (isAllTweet) {
//                removeTweet(statuses.get(0).getId());
//            }


        }

    }

    public void removeTweet(long id) {
        new RemoveTweet().execute(id);

    }

    class RemoveTweet extends AsyncTask<Long, Status, Status> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ClearTweetActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected twitter4j.Status doInBackground(Long... args) {
            twitter4j.Status status = null;

            try {

                status = twitter.destroyStatus(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(ClearTweetActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
//
//                    }
//                });                finish();
            }
            return status;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(twitter4j.Status user) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (user != null) {
                statuses.remove(user);
                clearTweetAdapter.setStatuses(statuses);
                clearTweetAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ClearTweetActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
            }
            if (isAllTweet && statuses.size() > 0) {
                removeTweet(statuses.get(0).getId());
            }
            else if (isAllTweet){
                page.setPage(++pageCount);
                new GetFollowersTask().execute();
            }


            // updating UI from Background Thread
        }

    }
    AdListener adListener= new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            int i = Utils.randInt(3);
            if (i==2) {
                mInterstitialAd.show();

            }

        }
    };
}
