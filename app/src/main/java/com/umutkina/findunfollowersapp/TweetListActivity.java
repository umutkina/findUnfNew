package com.umutkina.findunfollowersapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.umutkina.findunfollowersapp.adapters.TweetListAdapter;
import com.umutkina.findunfollowersapp.modals.TweetList;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TweetListActivity extends BaseActivity {

    @InjectView(R.id.tv_add_tweet_list)
    TextView tvAddTweetList;
    @InjectView(R.id.lv_tweet_list)
    ListView lvTweetList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);
        ButterKnife.inject(this);

        ArrayList<TweetList> tweetLists = getTweetLists();
        if (tweetLists == null) {
            tweetLists=new ArrayList<>();
        }
        TweetListAdapter tweetListAdapter=new TweetListAdapter(this);
        tweetListAdapter.setTweetLists(tweetLists);
        lvTweetList.setAdapter(tweetListAdapter);


    }


    @OnClick(R.id.tv_add_tweet_list)
    public void addTweetList(){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Tweet Listesi Oluştur");
        alert.setMessage("Tweet Listesine isim veriniz");
        final EditText input = new EditText(this);
        input.setHint("İsim");
        alert.setView(input);

            alert.setNegativeButton("İptal",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });

        alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name = input.getText().toString();
                AddTweetListItemActivity.newIntent(TweetListActivity.this,name);
            }
        });

        try {
            ((Activity) this).runOnUiThread(new Runnable() {
                public void run() {
                    alert.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


//        startActivity(new Intent(TweetListActivity.this,AddTweetListItemActivity.class));
    }
}
