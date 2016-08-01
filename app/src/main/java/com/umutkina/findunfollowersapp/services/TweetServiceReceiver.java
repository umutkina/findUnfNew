package com.umutkina.findunfollowersapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;

import com.umutkina.findunfollowersapp.R;
import com.umutkina.findunfollowersapp.UnfApplication;
import com.umutkina.findunfollowersapp.modals.Const;
import com.umutkina.findunfollowersapp.modals.HashTag;
import com.umutkina.findunfollowersapp.modals.HashTagListWraper;
import com.umutkina.findunfollowersapp.modals.TweetItem;
import com.umutkina.findunfollowersapp.modals.TweetList;
import com.umutkina.findunfollowersapp.modals.TweetListWrapper;
import com.umutkina.findunfollowersapp.modals.YdsWord;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by mac on 03/04/16.
 */
public class TweetServiceReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    Twitter twitter;
    int woeId = 23424969;
    Context context;
    ArrayList<YdsWord> ydsWords;
    HashTagListWraper hashTagListWraper;
    @Override
    public void onReceive(Context context, Intent intent) {


        Intent intentNew = new Intent(context, TweetService.class);
        intentNew.putExtra("foo", "bar");
        context.startService(intentNew);
        this.context = context;

        final UnfApplication application = (UnfApplication) context.getApplicationContext();
        twitter = application.getTwitter();
        SharedPreferences sharedPreferences = application.getmSharedPreferences();
        String string = sharedPreferences.getString(Const.TWEET_LIST, null);
        TweetListWrapper tweetListWrapper = (TweetListWrapper) Utils.getObject(string, TweetListWrapper.class);

        String hashTagJson=sharedPreferences.getString(Const.HASH_TAG_LIST,null );

        hashTagListWraper = (HashTagListWraper) Utils.getObject(string, HashTagListWraper.class);

        if (hashTagListWraper != null) {
//            Resources res = context.getResources();

//
//            String[] planets = res.getStringArray(R.array.hashtag_list);
            Random random = new Random();

            ArrayList<HashTag> hashTagArrayList = hashTagListWraper.getHashTagArrayList();
            int k = random.nextInt(hashTagArrayList.size());
            Query currentQuery = new Query(hashTagArrayList.get(k).getHashTag());
            search(currentQuery);
        }
        if (tweetListWrapper != null) {
            String name = sharedPreferences.getString(Const.SELECTED_TWEET_NAME, null);
            ArrayList<TweetList> tweetLists = tweetListWrapper.getTweetLists();
            TweetList currentTwetList = null;
            for (TweetList tweetList : tweetLists) {
                if (tweetList.getName().equalsIgnoreCase(name)) {
                    currentTwetList = tweetList;
                    break;
                }
            }
            Random random = new Random();
            ArrayList<TweetItem> tweetItems = currentTwetList.getTweetItems();
            int randonSentence = random.nextInt(tweetItems.size());

            new MentionReq().execute(tweetItems.get(randonSentence).getTweet());

        }
//

//        new GetLocaion().execute();




    }


    class MentionReq extends AsyncTask<String, String, String> {

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
        protected String doInBackground(String... args) {

            String user = "";
            try {
                twitter.updateStatus(args[0]);


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


            // updating UI from Background Thread
        }

    }

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


                Collections.shuffle(hastags);


                StringBuilder stringBuilder = new StringBuilder();

                ArrayList<String> strings = Utils.txtToArray(context);

                ydsWords = new ArrayList<>();
                for (int i = 0; i < strings.size() / 4; i++) {

                    YdsWord ydsWord = new YdsWord(0, strings.get(i * 4), strings.get(i * 4 + 1), strings.get(i * 4 + 2), strings.get(i * 4 + 3), 0);
                    ydsWords.add(ydsWord);
                }
                Resources res = context.getResources();

                String[] planets = res.getStringArray(R.array.sentence_list);
                Random random = new Random();

                int randonSentence = random.nextInt(planets.length);
                int i = random.nextInt(ydsWords.size());
                stringBuilder.append(ydsWords.get(i));

                YdsWord currentWord = ydsWords.get(i);

                String shareBody = "#yds Kelime: "+currentWord.getWord()+
                        "\nAnlamı : " + currentWord.getTranslatedWord() + "\nBenzer kelime: " + currentWord.getSimilarWord() + "\n" +context.getString(R.string.app_link);

                String lastTag = "";

                for (String hastag : hastags) {
                    lastTag = hastag;

                    stringBuilder.append(" " + hastag);

                    if (stringBuilder.toString().length() > 140) {
                        break;
                    }
                }
                String s = stringBuilder.toString();
                String replace = s.replace(lastTag, "");

                int randomnomber = random.nextInt(123);
//                new MentionReq().execute(shareBody);
                new MentionReq().execute(planets[randonSentence]);
//                new MentionReq().execute(planets[randonSentence]);

//                new MentionReq().execute("Bu tweet'i beğenen/rtleyenler ve hesabı takip edenler birbirini takip ediyor #takipedenitakipederim\n" +
//                        "#geritakip\n" +
//                        "#gt\n "+randomnomber);


            }

        }


    }


    // updating UI from Background Thread
    Query currentQuery;


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

            List<twitter4j.Status> tweets = result.getTweets();
            currentQuery = result.nextQuery();
            for (final twitter4j.Status l : tweets) {
                final User user = l.getUser();
                Random random = new Random();
                int i = random.nextInt(10000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        new RequestTask().execute(l.getId());
                        new RequestTaskFollow().execute(user.getId());

                    }
                }, i);


            }

            // updating UI from Background Thread
        }

    }

    class RequestTask extends AsyncTask<Long, String, String> {

        @Override
        protected String doInBackground(Long... uri) {
            try {
                twitter.createFavorite(uri[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }

    class RequestTaskFollow extends AsyncTask<Long, String, String> {

        @Override
        protected String doInBackground(Long... uri) {
            try {
                twitter.createFriendship(uri[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }


}
