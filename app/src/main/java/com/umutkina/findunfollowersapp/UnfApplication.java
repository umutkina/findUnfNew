package com.umutkina.findunfollowersapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.umutkina.findunfollowersapp.modals.Const;

import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by mac on 21/04/15.
 */
public class UnfApplication extends Application {
    private Twitter twitter;
    private SharedPreferences mSharedPreferences;
    User user;
//    ArrayList<Long> followerList;
//    ArrayList<Long> followingList;

    @Override
    public void onCreate() {
        super.onCreate();
        changeConfig();
//        if (android.os.Build.VERSION.SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

    }

    public Twitter changeConfig() {
        Random random = new Random();

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


        Configuration configuration = configurationBuilder.build();
        twitter = new TwitterFactory(configuration).getInstance();
        return twitter;
    }


//    public ArrayList<Long> getFollowerList() {
//        String followerList1 = mSharedPreferences.getString("followerList", null);
//        Type type = new TypeToken<List<Long>>() {
//        }.getType();
//        followerList = (ArrayList<Long>) Utils.getObject(followerList1, type);
//        return followerList;
//    }
//
//    public void setFollowerList(ArrayList<Long> followerList) {
//        String json = Utils.getJson(followerList);
//        mSharedPreferences.edit().putString("followerList", json).commit();
//        this.followerList = followerList;
//    }
//
//    public ArrayList<Long> getFollowingList() {
//        String followerList1 = mSharedPreferences.getString("followingList", null);
//        Type type = new TypeToken<List<Long>>() {
//        }.getType();
//        followingList = (ArrayList<Long>) Utils.getObject(followerList1, type);
//        return followingList;
//    }
//
//    public void setFollowingList(ArrayList<Long> followingList) {
//        String json = Utils.getJson(followingList);
//        mSharedPreferences.edit().putString("followingList", json).commit();
//        this.followingList = followingList;
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public SharedPreferences getmSharedPreferences() {
        return mSharedPreferences;
    }

    public void setmSharedPreferences(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }
}
