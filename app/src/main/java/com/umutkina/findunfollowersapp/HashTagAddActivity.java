package com.umutkina.findunfollowersapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.umutkina.findunfollowersapp.adapters.HashTagListAdapter;
import com.umutkina.findunfollowersapp.modals.Const;
import com.umutkina.findunfollowersapp.modals.HashTag;
import com.umutkina.findunfollowersapp.modals.HashTagListWraper;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HashTagAddActivity extends BaseActivity {

    @InjectView(R.id.et_tweet)
    EditText etTweet;
    @InjectView(R.id.tv_submit_tweet)
    TextView tvSubmitTweet;
    @InjectView(R.id.lv_tweet_item_list)
    ListView lvTweetItemList;
    @InjectView(R.id.tv_tweet_list_name)
    TextView tvTweetListName;
    ArrayList<HashTag> hashTagArrayList;
    HashTagListAdapter  hashTagListAdapter;
//    public static void newIntent(Context context, String name) {
//
//        Intent intent = new Intent(context, AddTweetListItemActivity.class);
//        intent.putExtra(TWEET_NAME, name);
//        context.startActivity(intent);
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tweet_list_item);
        ButterKnife.inject(this);

//        Bundle extras = getIntent().getExtras();
//        String string = extras.getString(TWEET_NAME);
        hashTagArrayList = getHashTagLists();

        tvTweetListName.setText("HashTag");


        hashTagListAdapter = new HashTagListAdapter(this);


        hashTagListAdapter.setHashTagArrayList(hashTagArrayList);
        lvTweetItemList.setAdapter(hashTagListAdapter);




    }

    public ArrayList<HashTag> getHashTagLists() {
        String string = mSharedPreferences.getString(Const.HASH_TAG_LIST, null);
        HashTagListWraper tweetListWrapper = (HashTagListWraper) Utils.getObject(string, HashTagListWraper.class);
        if (tweetListWrapper != null) {
            return tweetListWrapper.getHashTagArrayList();
        }

        return null;

    }

    @OnClick(R.id.tv_submit_tweet)
    public void submitTweet(){
        String tweet = etTweet.getText().toString();
        etTweet.setText("");
        HashTag hashTag= new HashTag();
        hashTag.setHashTag(tweet);
        hashTagArrayList.add(hashTag);

 ;

        saveTweetList(hashTagArrayList);

        hashTagListAdapter.setHashTagArrayList(hashTagArrayList);
        hashTagListAdapter.notifyDataSetChanged();

    }
    public void setHashTagListIttem(ArrayList<HashTag> hashTags){


        hashTagArrayList=hashTags;

        saveTweetList(hashTagArrayList);

        hashTagListAdapter.setHashTagArrayList(hashTagArrayList);
        hashTagListAdapter.notifyDataSetChanged();
    }

    public void saveTweetList(ArrayList<HashTag> hashTags) {
        HashTagListWraper hashTagListWraper= new HashTagListWraper();
        hashTagListWraper.setHashTagArrayList(hashTags);
        String json = Utils.getJson(hashTagListWraper);

        mSharedPreferences.edit().putString(Const.HASH_TAG_LIST,json).commit();
    }
}
