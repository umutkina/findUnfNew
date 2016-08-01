package com.umutkina.findunfollowersapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.umutkina.findunfollowersapp.modals.Const;
import com.umutkina.findunfollowersapp.utils.RoundedImageView;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class VenueDetailForPunchActivity extends BaseActivity {


    ArrayList<ArrayList<Long>> splittedUserIds;
    ArrayList<ArrayList<Long>> splittedFBUserIds;
    ArrayList<ArrayList<Long>> splittedMentionFollowerUserIds;
    ArrayList<ArrayList<Long>> splittedMentionFollowingUserIds;
    ProgressDialog pDialog;


    ArrayList<Long> unfollowersList = new ArrayList<>();
    ArrayList<Long> unfollowingList = new ArrayList<>();
    ArrayList<Long> followerList = new ArrayList<>();
    ArrayList<Long> followingList = new ArrayList<>();
    int counter = 0;
    // Twitter

    private static RequestToken requestToken;
    @InjectView(R.id.tv_header)
    TextView tvHeader;
    @InjectView(R.id.tv_followers_count)
    TextView tvFollowersCount;
    @InjectView(R.id.tv_following_count)
    TextView tvFollowingCount;
    @InjectView(R.id.tv_unfollowers_count)
    TextView tvUnfollowersCount;
    @InjectView(R.id.tv_unfollowing_count)
    TextView tvUnfollowingCount;
    @InjectView(R.id.tv_start_finding)
    TextView tvStartFinding;
    @InjectView(R.id.adView)
    AdView adView;
    @InjectView(R.id.iv_profile)
    RoundedImageView ivProfile;
    @InjectView(R.id.tv_user_name)
    TextView tvUserName;
    @InjectView(R.id.ll)
    LinearLayout ll;
    @InjectView(R.id.ll_button)
    LinearLayout llButton;
    @InjectView(R.id.tv_quick_tweet)
    TextView tvQuickTweet;
    @InjectView(R.id.tv_clear_tweet)
    TextView tvClearTweet;
    @InjectView(R.id.tv_search)
    TextView tvSearch;
    @InjectView(R.id.tv_search_mention)
    TextView tvSearchMention;
    @InjectView(R.id.ll_following)
    LinearLayout llFollowing;
    @InjectView(R.id.tv_mention)
    TextView tvMention;
    @InjectView(R.id.ll_mention)
    LinearLayout llMention;
    @InjectView(R.id.scrll)
    ScrollView scrll;
    @InjectView(R.id.tv_followers_mention)
    TextView tvFollowersMention;
    @InjectView(R.id.tv_following_mention)
    TextView tvFollowingMention;
    @InjectView(R.id.tv_clear_tweet_options)
    TextView tvClearTweetOptions;
    @InjectView(R.id.tv_clear_all)
    TextView tvClearAll;
    @InjectView(R.id.ll_clear)
    LinearLayout llClear;
    @InjectView(R.id.tv_unfollowers_mention)
    TextView tvUnfollowersMention;
    @InjectView(R.id.tv_unfollowing_mention)
    TextView tvUnfollowingMention;
    @InjectView(R.id.tv_direct_message)
    TextView tvDirectMessage;
    @InjectView(R.id.tv_tweet_list)
    TextView tvTweetList;

    private Uri uri;
    private long followerId = -1;
    long userID;
    // Shared Preferences
    private SharedPreferences mSharedPreferences;
    Twitter twitter;
    InterstitialAd mInterstitialAd;
    User user;
    long followerCursor = -1;
    long followingCursor = -1;
    boolean isLoginBefore;
    AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ButterKnife.inject(this);
        mSharedPreferences = getApplicationContext().getSharedPreferences("myprefs",
                Context.MODE_PRIVATE);
        boolean instashare = mSharedPreferences.getBoolean("instashare", false);
        if (!instashare) {
            showInstashareDialog();
        }
        mSharedPreferences.edit().putBoolean("instashare", true).commit();

        AdRequest adRequest = new AdRequest.Builder().build();


        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(adListener2);
        twitter = ((UnfApplication) getApplication()).getTwitter();


        isLoginBefore = mSharedPreferences.getBoolean("isLoginBefore", false);
        tvStartFinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTwitterLoggedInAlready()) {

                    loginToTwitter();
                } else {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            logout();
                        }
                    };
                    Utils.showInfoDialog(getContext(), runnable, getString(R.string.warning), getString(R.string.are_you_sure));
                }


            }
        });
        tvUnfollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedUserIds != null && splittedUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), UnfListActivity.class);
                    intent.putExtra("splittedIds", splittedUserIds);
                    startActivityForResult(intent, 2);
                } else {
                    if (splittedUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_unfollowers), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        tvUnfollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedFBUserIds != null && splittedFBUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), FollowBackActivity.class);
                    intent.putExtra("splittedFBIds", splittedFBUserIds);

                    startActivityForResult(intent, 2);
                } else {
                    if (splittedFBUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedFBUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_following), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        llFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedMentionFollowingUserIds != null && splittedMentionFollowingUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), UnfListActivity.class);
                    intent.putExtra("splittedIds", splittedMentionFollowingUserIds);
                    intent.putExtra("mentionType", 0);

                    startActivityForResult(intent, 2);
                } else {
                    if (splittedMentionFollowingUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedMentionFollowingUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_following), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tvFollowersMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedMentionFollowerUserIds != null && splittedMentionFollowerUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), MakeMentionActivity.class);
                    intent.putExtra("splittedIds", splittedMentionFollowerUserIds);
                    intent.putExtra("mentionType", 0);

                    startActivityForResult(intent, 2);
                } else {
                    if (splittedMentionFollowerUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedMentionFollowerUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_following), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tvFollowingMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedMentionFollowingUserIds != null && splittedMentionFollowingUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), MakeMentionActivity.class);
                    intent.putExtra("splittedIds", splittedMentionFollowingUserIds);
                    intent.putExtra("mentionType", 1);
                    startActivityForResult(intent, 2);
                } else {
                    if (splittedMentionFollowingUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedMentionFollowingUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_following), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tvUnfollowersMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedUserIds != null && splittedUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), MakeMentionActivity.class);
                    intent.putExtra("splittedIds", splittedUserIds);
                    intent.putExtra("mentionType", 0);

                    startActivityForResult(intent, 2);
                } else {
                    if (splittedUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_unfollowers), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tvUnfollowingMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedFBUserIds != null && splittedFBUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), MakeMentionActivity.class);
                    intent.putExtra("splittedIds", splittedFBUserIds);
                    intent.putExtra("mentionType", 0);

                    startActivityForResult(intent, 2);
                } else {
                    if (splittedFBUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedFBUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_following), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tvDirectMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedMentionFollowerUserIds != null && splittedMentionFollowerUserIds.size() > 0) {
                    Intent intent = new Intent(getContext(), DirectMessageActivity.class);
                    intent.putExtra("splittedIds", splittedMentionFollowerUserIds);
                    intent.putExtra("mentionType", 0);

                    startActivityForResult(intent, 2);
                } else {
                    if (splittedMentionFollowerUserIds == null) {
                        loginToTwitter();

                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
                    } else if (splittedMentionFollowerUserIds.size() == 0) {
                        Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_following), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tvQuickTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    startActivity(new Intent(VenueDetailForPunchActivity.this, MakeHashTagTweetActivity.class));
                } else {
                    loginToTwitter();

                    Toast.makeText(VenueDetailForPunchActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                }

            }
        });
        tvClearTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {

                    Intent intent = new Intent(VenueDetailForPunchActivity.this, ClearTweetActivity.class);
                    intent.putExtra("isAll", false);
                    startActivity(intent);
                } else {
                    loginToTwitter();

                    Toast.makeText(VenueDetailForPunchActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(VenueDetailForPunchActivity.this, ClearTweetActivity.class);
                            intent.putExtra("isAll", true);
                            startActivity(intent);
                        }
                    };

                    Utils.showInfoDialog(getContext(), runnable, getString(R.string.warning), getString(R.string.are_you_sure_all_tweet));

                } else {
                    loginToTwitter();

                    Toast.makeText(VenueDetailForPunchActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Intent intent = new Intent(VenueDetailForPunchActivity.this, SearchActivity.class);
                    intent.putExtra("followingIds", followingList);
                    startActivity(intent);
                } else {
                    loginToTwitter();

                    Toast.makeText(VenueDetailForPunchActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvSearchMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Intent intent = new Intent(VenueDetailForPunchActivity.this, SearchMentionActivity.class);
                    intent.putExtra("followingIds", followingList);
                    startActivity(intent);
                } else {
                    loginToTwitter();

                    Toast.makeText(VenueDetailForPunchActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                }
            }
        });
        followerId = mSharedPreferences.getLong("followerId", -1);


        tvMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendScroll();
//                        scrll.fullScroll(View.FOCUS_DOWN);

                llMention.setVisibility(llMention.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        tvClearTweetOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendScroll();
//                        scrll.fullScroll(View.FOCUS_DOWN);

                llClear.setVisibility(llClear.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });


    }

    private void showInstashareDialog() {
        new AlertDialog.Builder(this)
                .setTitle("InstaShare")
                .setMessage("InstaShare app help you download  and share Instagram image , videos if you want to download click yes button")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        String url = "https://play.google.com/store/apps/details?id=com.umutkina.instashare";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothingd
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrll.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNetworkAvailable()) {
            Utils.showInfoDialog(getContext(), null, getString(R.string.warning), getString(R.string.check_internet_connection));

        } else {
            if (isTwitterLoggedInAlready()) {
                if (isLoginBefore) {

                    tvStartFinding.setText(getString(R.string.logout));
                    tvStartFinding.setBackgroundColor(getResources().getColor(R.color.red_tatli_dk));
                    userID = mSharedPreferences.getLong(
                            Const.PREF_USER_ID, 0);
                    followerCursor = -1;
                    followingCursor = -1;
                    getFolowerList(userID);

                    AdRequest adRequestInter = new AdRequest.Builder().
//                addTestDevice("2795DA65D50FE4C721767208480E9ABC").
        build();

                    mInterstitialAd = new InterstitialAd(this);
                    mInterstitialAd.setAdUnitId("ca-app-pub-4443948134096736/4830862004");
                    mInterstitialAd.loadAd(adRequestInter);
                    mInterstitialAd.setAdListener(adListener);
                } else {
                    mSharedPreferences.edit().putBoolean("isLoginBefore", true).commit();
                    logout();
                }

            }
        }


    }


    public Activity getContext() {
        // TODO Auto-generated method stub
        return this;
    }


    class GetUser extends AsyncTask<ArrayList<Long>, ResponseList<User>, ResponseList<User>> {

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
        protected ResponseList<User> doInBackground(ArrayList<Long>... args) {
            ResponseList<User> users = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);

//                Long[] ids = new Long[args[0].size()];
//                ids=args[0].toArray(ids);
                users = twitter.lookupUsers(convertIntegers(args[0]));
            } catch (TwitterException e1) {
                e1.printStackTrace();
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
            if (users != null) {
                user = users.get(0);
                unfApplication.setUser(user);
                String name = user.getName();
                String biggerProfileImageURL = user.getBiggerProfileImageURL();
                tvUserName.setText(name + " / @" + user.getScreenName());
//            Picasso.with(VenueDetailForPunchActivity.this).load(biggerProfileImageURL).into(ivProfile);
                ivProfile.setImageUrlRounded(biggerProfileImageURL);

            }


            // updating UI from Background Thread
        }

    }

    public long[] convertIntegers(List<Long> longs) {
        long[] ret = new long[longs.size()];
        Iterator<Long> iterator = longs.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().longValue();
        }
        return ret;
    }


    private void loginToTwitter() {
        // Check if already logged in

        LoginTwitterTask task = new LoginTwitterTask();
        task.execute();


    }

    /**
     * Function to update status
     */
    private String askOAuth() {


        try {
            requestToken = twitter
                    .getOAuthRequestToken(Const.CALLBACK_URL_PUNCH + forceLogin());
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        if (requestToken != null) {
            return requestToken.getAuthenticationURL();
        }
        return null;

    }

    class LoginTwitterTask extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VenueDetailForPunchActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {

            return askOAuth();
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String authenticationUrl) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread

            if (authenticationUrl != null) {
                Intent intent = new Intent(VenueDetailForPunchActivity.this,
                        WebViewActivity.class);
                intent.putExtra("url", authenticationUrl);
                startActivityForResult(intent, 1);
            } else {
                Utils.showInfoDialog(getContext(), null, getString(R.string.warning), getString(R.string.check_internet_connection));
            }


            // VenueDetailForPunchActivity.this.startActivity(new Intent(
            // Intent.ACTION_VIEW, Uri.parse(authenticationUrl)));
        }

    }

    class TwitterGetAccesTokenTask extends
            AsyncTask<AccessToken, String, AccessToken> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VenueDetailForPunchActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected AccessToken doInBackground(AccessToken... args) {
            AccessToken accessToken = null;

            // oAuth verifier
            String verifier = uri
                    .getQueryParameter(Const.URL_TWITTER_OAUTH_VERIFIER);

            try {
                // Get the access token

                accessToken = twitter.getOAuthAccessToken(requestToken,
                        verifier);

                // Displaying in xml ui

            } catch (Exception e) {
                // Check log for login errors
                Log.e("Twitter Login Error", "> " + e.getMessage());
            }
            // save changes

            return accessToken;
        }


        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(AccessToken accessToken) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            // Shared Preferences
            Editor e = mSharedPreferences.edit();

            // After getting access token, access token secret
            // store them in application preferences
            e.putString(Const.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(Const.PREF_KEY_OAUTH_SECRET,
                    accessToken.getTokenSecret());
            e.putLong(Const.PREF_USER_ID, accessToken.getUserId());

            // Store login status - true
            e.putBoolean(Const.PREF_KEY_TWITTER_LOGIN, true);
            e.commit(); // save changes

            Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

            userID = accessToken.getUserId();
            tvStartFinding.setText(getString(R.string.logout));
            tvStartFinding.setBackgroundColor(getResources().getColor(R.color.red_tatli_dk));
            getFolowerList(userID);

            // try {
            //
            // // id = twitter.getId();
            // } catch (TwitterException e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // }

        }

    }

    public String forceLogin() {

        String token = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_TOKEN, null);

        return token == null ? "force_login=true" : "";

    }

    private void logout() {

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        final Editor edit = mSharedPreferences.edit();
        edit.remove(Const.PREF_KEY_OAUTH_TOKEN);
        edit.remove(Const.PREF_KEY_OAUTH_SECRET);
        edit.remove(Const.PREF_USER_ID);

        edit.remove(Const.PREF_KEY_TWITTER_LOGIN);
        edit.commit();

        unfollowersList.clear();
        unfollowingList.clear();

        followerList.clear();
        followingList.clear();
        if (splittedMentionFollowerUserIds != null) {
            splittedMentionFollowerUserIds.clear();
        }
        if (splittedUserIds != null) {
            splittedUserIds.clear();
        }
        if (splittedFBUserIds != null) {
            splittedFBUserIds.clear();
        }
        if (splittedMentionFollowingUserIds != null) {
            splittedMentionFollowingUserIds.clear();
        }

        tvUserName.setText("User Name");
        ivProfile.setImageResource(R.drawable.profile);
        tvUnfollowersCount.setText(getString(R.string.unfollowers));
        tvUnfollowingCount.setText(getString(R.string.unfollowing));
        tvFollowersCount.setText("---");
        tvFollowingCount.setText("---");


        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
        mSharedPreferences = getApplicationContext().getSharedPreferences("myprefs",
                Context.MODE_PRIVATE);
        String accestoken = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_TOKEN, null);
        String accesSecret = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_SECRET, null);
        if (accesSecret != null) {
            configurationBuilder.setOAuthAccessToken(accestoken);
            configurationBuilder.setOAuthAccessTokenSecret(accesSecret);
        }

        tvStartFinding.setText(getString(R.string.login));
        tvStartFinding.setBackgroundColor(getResources().getColor(R.color.Blue));
        Configuration configuration = configurationBuilder.build();
        twitter = new TwitterFactory(configuration).getInstance();
        ((UnfApplication) getApplication()).setTwitter(twitter);

    }

    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(Const.PREF_KEY_TWITTER_LOGIN,
                false);
    }

    private void getFolowerList(long userID) {
        this.userID = userID;
        unfollowersList.clear();
        unfollowingList.clear();
        followerList.clear();
        followingList.clear();
        if (splittedMentionFollowerUserIds != null) {
            splittedMentionFollowerUserIds.clear();
            splittedUserIds.clear();
            splittedFBUserIds.clear();
            splittedMentionFollowingUserIds.clear();
        }


        new GetFollowersTask().execute(userID);

        ArrayList<Long> longs = new ArrayList<>();
        longs.add(Long.parseLong(userID + ""));

        new GetUser().execute(longs);


    }


    class GetFollowersTask extends AsyncTask<Long, IDs, IDs> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VenueDetailForPunchActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected IDs doInBackground(Long... args) {
            IDs followersList = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);
                System.out.println("mycursor followerCursor : " + followerCursor);
                followersList = twitter.getFollowersIDs(args[0], followerCursor);

            } catch (TwitterException e1) {
                e1.printStackTrace();
            }
            return followersList;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(IDs iDs) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (iDs == null) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        logout();
                    }
                };
                Utils.showInfoDialogNotCancelable(getContext(), runnable, getString(R.string.warning), getString(R.string.limit_asim));
            } else {
                long[] iDs1 = iDs.getIDs();

                for (long l : iDs1) {
                    followerList.add(l);
                }
                int remaining = iDs.getRateLimitStatus().getRemaining();
                if (iDs.hasNext() && remaining != 0) {
                    followerCursor = iDs.getNextCursor();
                    new GetFollowersTask().execute(userID);
                } else if (remaining == 0) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    };
                    Utils.showInfoDialog(getContext(), runnable, getString(R.string.warning), getString(R.string.limit_asim));
                } else {
                    tvFollowersCount.setText("" + followerList.size());
                    new GetFollowedTask().execute(userID);
                }
            }


        }

    }

    class GetFollowedTask extends AsyncTask<Long, IDs, IDs> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VenueDetailForPunchActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected IDs doInBackground(Long... args) {
            IDs followersList = null;
            try {

//                followersList = twitter.getFollowersList(args[0], args[1]);
                System.out.println("mycursor followingCursor : " + followingCursor);
                followersList = twitter.getFriendsIDs(args[0], followingCursor);

            } catch (TwitterException e1) {
                e1.printStackTrace();
            }
            return followersList;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(IDs iDs) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            RateLimitStatus rateLimitStatus = iDs.getRateLimitStatus();
            int limit = rateLimitStatus.getLimit();
            System.out.println("rate limit " + limit);
            long[] iDs3 = iDs.getIDs();
            int remaining = rateLimitStatus.getRemaining();

            for (long l : iDs3) {
                followingList.add(l);
            }
            if (iDs.hasNext() && remaining != 0) {
                followingCursor = iDs.getNextCursor();
                new GetFollowedTask().execute(userID);
            } else if (remaining == 0) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                };
                Utils.showInfoDialog(getContext(), runnable, getString(R.string.warning), getString(R.string.limit_asim));
            } else {


                tvFollowingCount.setText("" + followingList.size());

                // updating UI from Background Thread
                ArrayList<Long> unfollowers = unfollowers(followerList, followingList);
                ArrayList<Long> unfollowings = unfollowings(followerList, followingList);
                tvUnfollowersCount.setText(getString(R.string.unfollowers) + "  " + unfollowers.size());
                tvUnfollowingCount.setText(getString(R.string.unfollowing) + "  " + unfollowings.size());
                splittedUserIds = splittedUserIds(unfollowers);

                splittedFBUserIds = splittedUserIds(unfollowings);
                splittedMentionFollowerUserIds = splittedUserIds(followerList);
                splittedMentionFollowingUserIds = splittedUserIds(followingList);
            }

        }

    }

    private ArrayList<ArrayList<Long>> splittedUserIds(ArrayList<Long> userIds) {

        ArrayList<ArrayList<Long>> splittedIds = new ArrayList<>();
        ArrayList<Long> userIdOf20 = new ArrayList<>();
        int i1 = userIds.size() / 20;
        for (int i = 0; i < userIds.size(); i++) {
            userIdOf20.add(userIds.get(i));

            if ((i + 1) % 20 == 0) {


                splittedIds.add(userIdOf20);
                userIdOf20 = new ArrayList<>();
            } else if (i == (userIds.size() - 1)) {
                splittedIds.add(userIdOf20);
                userIdOf20 = new ArrayList<>();
            }


        }


        return splittedIds;

    }


    public ArrayList<Long> unfollowers(ArrayList<Long> followerIds, ArrayList<Long> followedIds) {

//        long[] followedIds = followedIDs.getIDs();
//        long[] followerIds = followersIDs.getIDs();

        for (long followedId : followedIds) {
            int counter = 0;
            for (long id : followerIds) {
                if (followedId == id) {
                    break;
                }
                counter++;
            }
//            System.out.println( "counter : " +counter+" followedCount : "+ followedIds.length);
            if (counter == followerIds.size()) {
                unfollowersList.add(followedId);
            }
        }

        return unfollowersList;
    }

    public ArrayList<Long> unfollowings(ArrayList<Long> followerIds, ArrayList<Long> followedIds) {

//        long[] followedIds = followedIDs.getIDs();
//        long[] followerIds = followersIDs.getIDs();

        for (long followedId : followerIds) {
            int counter = 0;
            for (long id : followedIds) {
                if (followedId == id) {
                    break;
                }
                counter++;
            }
//            System.out.println( "counter : " +counter+" followedCount : "+ followedIds.length);
            if (counter == followedIds.size()) {
                unfollowingList.add(followedId);
            }
        }

        return unfollowingList;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (data != null && data.getStringExtra("url") != null) {

                String stringExtra = data.getStringExtra("url");
                uri = Uri.parse(stringExtra);
                if (uri != null
                        && uri.toString().startsWith(
                        Const.CALLBACK_URL_PUNCH)) {
                    TwitterGetAccesTokenTask accesTokenTask = new TwitterGetAccesTokenTask();
                    accesTokenTask.execute();
                }
            }
        }
//        else{
//            if (!isTwitterLoggedInAlready()) {
//
//                loginToTwitter();
//            } else {
//                userID = mSharedPreferences.getLong(
//                        Const.PREF_USER_ID, 0);
//                getFolowerList(userID);
//            }
//        }
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

    @OnClick(R.id.tv_tweet_list)
    public void tweetList() {
        startActivity(new Intent(getContext(),TweetListActivity.class));
    }
    @OnClick(R.id.tv_hashtag_add)
    public void hashTagAdd() {
        startActivity(new Intent(getContext(),HashTagAddActivity.class));
    }

    @OnClick(R.id.tv_start_tweet)
    public void startTweet() {

        if (splittedUserIds != null && splittedUserIds.size() > 0) {

            sendLocation();
        } else {
            if (splittedUserIds == null) {
                loginToTwitter();

                Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.shoult_login), Toast.LENGTH_SHORT).show();
            } else if (splittedUserIds.size() == 0) {
                Toast.makeText(VenueDetailForPunchActivity.this, getString(R.string.no_unfollowers), Toast.LENGTH_SHORT).show();
            }
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
            }, 2000);

        }
    };


}
