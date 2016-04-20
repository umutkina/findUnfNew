package com.umutkina.findunfollowersapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.umutkina.findunfollowersapp.adapters.UnfListAdapter;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class UnfListActivity extends BaseActivity {
    private static Twitter twitter;
    ProgressDialog pDialog;
    int counter = 0;
    @InjectView(R.id.lv_more)
    ListView lvMore;
    @InjectView(R.id.tv_more)
    TextView tvMore;

    ArrayList<User> usersAll = new ArrayList<>();
    UnfListAdapter unfListAdapter;
    private int friendCount = 0;
    InterstitialAd mInterstitialAd;
    AdView mAdView;
     ArrayList<ArrayList<Long>> splittedIds;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unf_list);
        ButterKnife.inject(this);
        ;

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(adListener2);
        twitter = ((UnfApplication) getApplication()).getTwitter();
        unfListAdapter = new UnfListAdapter(this);
        lvMore.setAdapter(unfListAdapter);

        splittedIds = (ArrayList<ArrayList<Long>>) getIntent().getSerializableExtra("splittedIds");
        new GetUnFollowerUser().execute(splittedIds.get(counter));
        System.out.println("array count : " + splittedIds.size());
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedIds.size() -1> counter) {
                    new GetUnFollowerUser().execute(splittedIds.get(++counter));
                }
                int i = Utils.randInt(3);
                if (i==2) {
                    mInterstitialAd.show();

                }

            }
        });

        AdRequest adRequestInter = new AdRequest.Builder().
//                addTestDevice("2795DA65D50FE4C721767208480E9ABC").
        build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4443948134096736/4830862004");
        mInterstitialAd.loadAd(adRequestInter);
        mInterstitialAd.setAdListener(adListener);
    }


    public void unf(long id) {
        new UnfollowReq().execute(id);

    }

    class GetUnFollowerUser extends AsyncTask<ArrayList<Long>, ResponseList<User>, ResponseList<User>> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(UnfListActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected ResponseList<User> doInBackground(ArrayList<Long>... args) {
            ResponseList<User> users = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);

//                Long[] ids = new Long[args[0].size()];
//                ids=args[0].toArray(ids);
                users = twitter.lookupUsers(convertIntegers(args[0]));
            } catch (Exception e1) {
                e1.printStackTrace();
//                Toast.makeText(UnfListActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                finish();
            }
            return users;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(ResponseList<User> users) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            usersAll.addAll((ArrayList<User>) users);
            unfListAdapter.setUsers(usersAll);
            unfListAdapter.notifyDataSetChanged();
            for (User l : users) {
                System.out.println(friendCount++ + "friend name : " + l.getName());
            }
            unf(usersAll.get(0).getId());
            // updating UI from Background Thread
        }

    }

    class UnfollowReq extends AsyncTask<Long, User, User> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(UnfListActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected User doInBackground(Long... args) {

            User user=null;
            try {

                user = twitter.destroyFriendship(args[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return  user;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(User user) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (user!=null) {
                usersAll.remove(user);
//                unfApplication.getFollowingList().remove(user.getId());
                unfListAdapter.setUsers(usersAll);
                unfListAdapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(UnfListActivity.this,"An error occured If the problem persists, please update",Toast.LENGTH_SHORT).show();
            }
            if (usersAll.size()>0) {
                Random random = new Random();
                int i = random.nextInt(1000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        unf(usersAll.get(0).getId() );

                    }
                }, 1000);

            }
            if (usersAll.size()==0) {
                new GetUnFollowerUser().execute(splittedIds.get(++counter));
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

    AdListener adListener2 = new AdListener() {
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

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mAdView.setVisibility(View.VISIBLE);
                    ;
                }
            }, 2000);
        }
    };
    public long[] convertIntegers(List<Long> longs) {
        long[] ret = new long[longs.size()];
        Iterator<Long> iterator = longs.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().longValue();
        }
        return ret;
    }


}
