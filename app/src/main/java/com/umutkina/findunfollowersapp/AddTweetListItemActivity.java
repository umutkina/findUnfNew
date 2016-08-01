package com.umutkina.findunfollowersapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.umutkina.findunfollowersapp.adapters.TwweetListItemAdapter;
import com.umutkina.findunfollowersapp.modals.Const;
import com.umutkina.findunfollowersapp.modals.TweetItem;
import com.umutkina.findunfollowersapp.modals.TweetList;
import com.umutkina.findunfollowersapp.modals.TweetListWrapper;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddTweetListItemActivity extends BaseActivity {
    static final String TWEET_NAME = "tweetName";
    TweetList currentTwetList;
    @InjectView(R.id.et_tweet)
    EditText etTweet;
    @InjectView(R.id.tv_submit_tweet)
    TextView tvSubmitTweet;
    @InjectView(R.id.lv_tweet_item_list)
    ListView lvTweetItemList;
    @InjectView(R.id.tv_tweet_list_name)
    TextView tvTweetListName;
    ArrayList<TweetItem> tweetItems;
    TwweetListItemAdapter twweetListItemAdapter;
    public static void newIntent(Context context, String name) {

        Intent intent = new Intent(context, AddTweetListItemActivity.class);
        intent.putExtra(TWEET_NAME, name);
        context.startActivity(intent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tweet_list_item);
        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();
        String string = extras.getString(TWEET_NAME);
        ArrayList<TweetList> tweetLists = getTweetLists();
        if (tweetLists==null) {
            tweetLists=new ArrayList<>();
            TweetList tweetList=new TweetList();
            tweetList.setName(string);
            tweetList.setTweetItems(new ArrayList<TweetItem>());
            tweetLists.add(tweetList);
            saveTweetList(tweetLists);

        }


        for (TweetList tweetList : tweetLists) {
            if (tweetList.getName().equalsIgnoreCase(string)) {
                currentTwetList = tweetList;
                break;
            }
        }
        if (currentTwetList==null) {
            TweetList tweetList=new TweetList();
            tweetList.setName(string);
            tweetList.setTweetItems(new ArrayList<TweetItem>());
            tweetLists.add(tweetList);
            saveTweetList(tweetLists);
            currentTwetList=tweetList;
        }

        String name = currentTwetList.getName();
        tvTweetListName.setText(name);


        twweetListItemAdapter = new TwweetListItemAdapter(this);


        tweetItems = currentTwetList.getTweetItems();
        twweetListItemAdapter.setTweetItems(tweetItems);
        lvTweetItemList.setAdapter(twweetListItemAdapter);



    }

    @OnClick(R.id.tv_submit_tweet)
    public void submitTweet(){
        String tweet = etTweet.getText().toString();
        etTweet.setText("");
        TweetItem tweetItem= new TweetItem();
        tweetItem.setTweet(tweet);
        tweetItems.add(tweetItem);
        currentTwetList.setTweetItems(tweetItems);
        String name = currentTwetList.getName();
        ArrayList<TweetList> tweetLists = getTweetLists();
        for (int i = 0; i < tweetLists.size(); i++) {
            TweetList tweetList = tweetLists.get(i);
            if (tweetList.getName().equalsIgnoreCase(name)) {
                tweetLists.remove(i);

                break;
            }
        }
        tweetLists.add(currentTwetList);

        saveTweetList(tweetLists);
        tweetItems = currentTwetList.getTweetItems();
        twweetListItemAdapter.setTweetItems(tweetItems);
        twweetListItemAdapter.notifyDataSetChanged();


    }
    public void setTweetListIttem(ArrayList<TweetItem> tweetListIttem){

        String name = currentTwetList.getName();
        currentTwetList.setTweetItems(tweetListIttem);
        ArrayList<TweetList> tweetLists = getTweetLists();
        for (int i = 0; i < tweetLists.size(); i++) {
            TweetList tweetList = tweetLists.get(i);
            if (tweetList.getName().equalsIgnoreCase(name)) {
                tweetLists.remove(i);

                break;
            }
        }
        tweetLists.add(currentTwetList);

        saveTweetList(tweetLists);
        tweetItems = currentTwetList.getTweetItems();
        twweetListItemAdapter.setTweetItems(tweetItems);
        twweetListItemAdapter.notifyDataSetChanged();
    }

    public void saveTweetList(ArrayList<TweetList> tweetLists) {
        TweetListWrapper tweetListWrapper= new TweetListWrapper();
        tweetListWrapper.setTweetLists(tweetLists);
        String json = Utils.getJson(tweetListWrapper);

        mSharedPreferences.edit().putString(Const.TWEET_LIST,json).commit();
    }
}
