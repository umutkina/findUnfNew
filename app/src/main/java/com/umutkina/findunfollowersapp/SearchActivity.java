package com.umutkina.findunfollowersapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.umutkina.findunfollowersapp.adapters.FollowSearchListAdapter;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class SearchActivity extends BaseActivity {

    @InjectView(R.id.tv_mention)
    TextView tvMention;
    @InjectView(R.id.et_mention)
    EditText etMention;
    @InjectView(R.id.rl_tv_et)
    RelativeLayout rlTvEt;
    @InjectView(R.id.tv_save_edit)
    TextView tvSaveEdit;
    @InjectView(R.id.rl_mention)
    RelativeLayout rlMention;
    @InjectView(R.id.lv_hashtag)
    ListView lvHashtag;
    @InjectView(R.id.tv_submit)
    TextView tvSubmit;
    @InjectView(R.id.adView)
    AdView adView;
    Twitter twitter;
    String mentionText;
    long id;
    ProgressDialog pDialog;
    Query currentQuery;
    ArrayList<User> usersAll = new ArrayList<>();
    ArrayList<User> usersAllTemp = new ArrayList<>();
    ArrayList<Long> followingIds;

    FollowSearchListAdapter followListAdapter;

    InterstitialAd mInterstitialAd;
    AdView mAdView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        followingIds = (ArrayList<Long>) getIntent().getSerializableExtra("followingIds");

        id = unfApplication.getUser().getId();
        twitter = ((UnfApplication) getApplication()).getTwitter();
        followListAdapter = new FollowSearchListAdapter(this);
        lvHashtag.setAdapter(followListAdapter);


        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AdRequest adRequestInter = new AdRequest.Builder().

//                addTestDevice("2795DA65D50FE4C721767208480E9ABC").
        build();
        mAdView.setAdListener(adListener2);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4443948134096736/4830862004");
        mInterstitialAd.loadAd(adRequestInter);
        mInterstitialAd.setAdListener(adListener);

        mentionText = mSharedPreferences.getString("searchText" + id, null);
        if (mentionText != null) {
            etMention.setText(mentionText);
            etMention.setVisibility(View.GONE);
            tvMention.setText(mentionText);
            tvMention.setVisibility(View.VISIBLE);
            tvSaveEdit.setText(getString(R.string.edit));
        }
        tvSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSaveEdit.getText().toString().equalsIgnoreCase(getString(R.string.save))) {
                    tvMention.setText(etMention.getText().toString());
                    etMention.setVisibility(View.GONE);
                    tvMention.setVisibility(View.VISIBLE);
                    tvSaveEdit.setText(getString(R.string.edit));
                    String value = tvMention.getText().toString();

                    mSharedPreferences.edit().putString("searchText" + unfApplication.getUser().getId(), value).commit();


                } else {
                    currentQuery = null;
                    tvMention.setText("");
                    tvSaveEdit.setText(getString(R.string.save));
                    etMention.setVisibility(View.VISIBLE);
                    tvMention.setVisibility(View.GONE);

                }


            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvMention.getText().toString().length() > 0) {
                    mentionText = tvMention.getText().toString();
                    if (currentQuery == null) {
                        currentQuery = new Query(mentionText);
                    }


                    search(currentQuery);
                }


            }
        });
    }

    public void follow(long id) {
        new FollowReq().execute(id);

    }

    class FollowReq extends AsyncTask<Long, User, User> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected User doInBackground(Long... args) {

            User user = null;
            try {

                user = twitter.createFriendship(args[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return user;
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
            if (user != null) {
                usersAll.remove(user);
                followListAdapter.setUsers(usersAll);
                followListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(SearchActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
            }


            // updating UI from Background Thread
        }

    }


    public void spam(long id) {
        new SpamReq().execute(id);

    }

    class SpamReq extends AsyncTask<Long, User, User> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected User doInBackground(Long... args) {

            User user = null;
            try {

                user = twitter.reportSpam(args[0]);
//                twitter.createBlock(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(SearchActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
//
//                    }
//                });                finish();
            }
            return user;
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
            if (user != null) {
                usersAll.remove(user);
                followListAdapter.setUsers(usersAll);
                followListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(SearchActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
            }


            // updating UI from Background Thread
        }

    }
    public void search(Query query) {
        ;
        new SearchRequest().execute(query);
    }

    class SearchRequest extends AsyncTask<Query, QueryResult, QueryResult> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected QueryResult doInBackground(Query... args) {
            QueryResult search = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);

//                Long[] ids = new Long[args[0].size()];
//                ids=args[0].toArray(ids);

                search = twitter.search(args[0]);
            } catch (TwitterException e1) {
                e1.printStackTrace();
            }
            return search;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(QueryResult result) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            List<twitter4j.Status> tweets = result.getTweets();
            currentQuery = result.nextQuery();
            for (twitter4j.Status l : tweets) {
                User user = l.getUser();
                long id1 = user.getId();

                int counter = 0;
                for (Long followingId : followingIds) {
                    if (followingId == id1) {
                        break;
                    }
                    counter++;
                }
                if (counter == followingIds.size()) {
                    int counterNewUser=0;
                    for (User user1 : usersAllTemp) {
                        if (user1.getId() == id1) {
                            break;
                        }
                        counterNewUser++;
                    }
                    if (counterNewUser==usersAllTemp.size()) {
                        usersAll.add(user);
                        usersAllTemp.add(user);
                    }
                }

            }
            followListAdapter.setUsers(usersAll);
            followListAdapter.notifyDataSetChanged();
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
    AdListener adListener2= new AdListener() {
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


}
