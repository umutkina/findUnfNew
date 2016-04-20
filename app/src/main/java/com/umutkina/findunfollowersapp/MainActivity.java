package com.umutkina.findunfollowersapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.umutkina.findunfollowersapp.modals.Const;
import com.umutkina.findunfollowersapp.utils.Utils;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class MainActivity extends BaseActivity {
    ProgressDialog pDialog;
    private static RequestToken requestToken;
    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (isTwitterLoggedInAlready()) {
            startActivity(new Intent(MainActivity.this, VenueDetailForPunchActivity.class));
            finish();
//        }
//        else{
//            loginToTwitter();
//        }

    }

    private void loginToTwitter() {
        // Check if already logged in

        LoginTwitterTask task = new LoginTwitterTask();
        task.execute();


    }

    class LoginTwitterTask extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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
                Intent intent = new Intent(MainActivity.this,
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

    private String askOAuth() {


        try {
            requestToken = twitter
                    .getOAuthRequestToken(Const.CALLBACK_URL_PUNCH + forceLogin());
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();

                }
            });            finish();
        }

        if (requestToken != null) {
            return requestToken.getAuthenticationURL();
        }
        return null;

    }

    public Activity getContext() {
        // TODO Auto-generated method stub
        return this;
    }

    public String forceLogin() {

        String token = mSharedPreferences.getString(Const.PREF_KEY_OAUTH_TOKEN, null);

        return token == null ? "force_login=true" : "";

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

    }

    class TwitterGetAccesTokenTask extends
            AsyncTask<AccessToken, String, AccessToken> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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
            SharedPreferences.Editor e = mSharedPreferences.edit();

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
            startActivity(new Intent(MainActivity.this, VenueDetailForPunchActivity.class));
            finish();

            // try {
            //
            // // id = twitter.getId();
            // } catch (TwitterException e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // }

        }

    }
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(Const.PREF_KEY_TWITTER_LOGIN,
                false);
    }

}
