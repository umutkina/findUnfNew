package com.umutkina.findunfollowersapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class MakeHashTagTweetActivity extends BaseActivity {

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
    Twitter twitter;

    int counter = 0;

    ProgressDialog pDialog;
    String mentionText;
    @InjectView(R.id.tv_submit)
    TextView tvSubmit;
    int woeId = 23424969;
    InterstitialAd mInterstitialAd;
    long id = 0;
    @InjectView(R.id.tv_header)
    TextView tvHeader;
    @InjectView(R.id.iv_country)
    ImageView ivCountry;
    @InjectView(R.id.rl_header)
    RelativeLayout rlHeader;
    @InjectView(R.id.adView)
    AdView adView;
    ArrayAdapter<String> itemsAdapter;
    AdView mAdView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_hash_tag_tweet);
        ButterKnife.inject(this);
        AdRequest adRequest = new AdRequest.Builder().build();



        mAdView = (AdView) findViewById(R.id.adView);

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(adListener2);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequestInter = new AdRequest.Builder().
//                addTestDevice("2795DA65D50FE4C721767208480E9ABC").
        build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4443948134096736/4830862004");
        mInterstitialAd.loadAd(adRequestInter);
        mInterstitialAd.setAdListener(adListener);

        twitter = unfApplication.getTwitter();


        id = unfApplication.getUser().getId();

        mentionText = mSharedPreferences.getString("hashTag" + id, null);

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

                    mSharedPreferences.edit().putString("hashTag" + id, value).commit();

                } else {
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
                counter++;

                mention();
            }
        });
        ivCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MakeHashTagTweetActivity.this, SelectCountry.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String displayLanguage = Locale.getDefault().getDisplayLanguage();
        System.out.println("displayLanguage : " + displayLanguage);
        woeId = (int) mSharedPreferences.getLong("woeId", 0);
        if (woeId == 0) {
            if (!displayLanguage.equalsIgnoreCase("Türkçe")) {
                woeId = 1;
            }
        }
        if (itemsAdapter != null) {
            itemsAdapter.clear();
            itemsAdapter.notifyDataSetChanged();
        }
        new GetLocaion().execute();
    }

    public void mention() {
        if (tvMention.getText().toString().length() > 0 && (tvMention.getText().toString().length() + (counter + " ").length()) < 140) {
            new MentionReq().execute();

        } else if (tvMention.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.enter_text), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.short_of_140), Toast.LENGTH_SHORT).show();

        }


    }


    AdListener adListener = new AdListener() {
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
            if (i == 2) {
                mInterstitialAd.show();

            }

        }
    };

    class GetLocaion extends AsyncTask<Void, Trends, Trends> {


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
        protected Trends doInBackground(Void... params) {
            Trends locations = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);

//                Long[] ids = new Long[args[0].size()];
//                ids=args[0].toArray(ids);


                locations = twitter.getPlaceTrends(woeId);
            } catch (Exception e1) {
                e1.printStackTrace();
//                finish();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MakeHashTagTweetActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
//
//                    }
//                });

            }
            return locations;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(Trends trends) {
            // dismiss the dialog after getting all products
            if (trends != null) {
                final ArrayList<String> hastags = new ArrayList<>();
                for (int i = 0; i < trends.getTrends().length; i++) {
                    System.out.println("trends : " + trends.getTrends()[i].getName());
                    hastags.add(trends.getTrends()[i].getName());
                }


                itemsAdapter = new ArrayAdapter<String>(MakeHashTagTweetActivity.this, android.R.layout.simple_list_item_1, hastags);
                lvHashtag.setAdapter(itemsAdapter);
                lvHashtag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s = hastags.get(position);
                        etMention.setText(etMention.getText() + " " + s);
                    }
                });

            }

        }


        // updating UI from Background Thread
    }

    class MentionReq extends AsyncTask<User, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MakeHashTagTweetActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(User... args) {

            String user = "";
            try {
                twitter.updateStatus(tvMention.getText().toString() + " " + counter);


            } catch (TwitterException e) {
                e.printStackTrace();
                user = null;
            }
            return user;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String user) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (user != null) {
                Toast.makeText(MakeHashTagTweetActivity.this, "Send", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MakeHashTagTweetActivity.this, "An error occured If the problem persists, please update", Toast.LENGTH_SHORT).show();
            }


            // updating UI from Background Thread
        }

    }

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
            }, 3000);
        }
    };

}


