package com.umutkina.findunfollowersapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.umutkina.findunfollowersapp.services.TweetServiceReceiver;

import twitter4j.Twitter;
import twitter4j.User;

/**
 * Created by mac on 01/05/15.
 */
public class BaseActivity extends Activity {
    UnfApplication unfApplication;
    SharedPreferences mSharedPreferences;
    User user;
    Twitter twitter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unfApplication = (UnfApplication) getApplication();
        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable("user");
            twitter = (Twitter) savedInstanceState.getSerializable("twitter");
            unfApplication.setUser(user);
        }
        else{
            user=  unfApplication.getUser();
            twitter=unfApplication.getTwitter();
        }

        mSharedPreferences = unfApplication.getmSharedPreferences();


    }
        public void sendLocation() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), TweetServiceReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, TweetServiceReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1800000, pIntent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("twitter", twitter);
        outState.putSerializable("user", user);
    }
}
