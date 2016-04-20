package com.umutkina.findunfollowersapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.umutkina.findunfollowersapp.adapters.MentionListAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class MakeMentionActivity extends BaseActivity {
    private static Twitter twitter;
    ProgressDialog pDialog;
    int counter = 0;
    @InjectView(R.id.lv_more)
    ListView lvMore;
    @InjectView(R.id.tv_more)
    TextView tvMore;

    ArrayList<User> usersAll = new ArrayList<>();
    MentionListAdapter unfListAdapter;
    @InjectView(R.id.tv_header)
    TextView tvHeader;
    @InjectView(R.id.et_mention)
    EditText etMention;
    @InjectView(R.id.tv_save_edit)
    TextView tvSaveEdit;
    @InjectView(R.id.rl_mention)
    RelativeLayout rlMention;
    @InjectView(R.id.adView)
    AdView adView;
    @InjectView(R.id.tv_mention)
    TextView tvMention;
    private int friendCount = 0;
    boolean isTrMention = false;
    int screenType;
    String mentionText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_mention);
        ButterKnife.inject(this);
        AdRequest adRequest = new AdRequest.Builder().build();


        AdView mAdView = (AdView) findViewById(R.id.adView);

        mAdView.loadAd(adRequest);

        twitter = ((UnfApplication) getApplication()).getTwitter();
        unfListAdapter = new MentionListAdapter(this);
        lvMore.setAdapter(unfListAdapter);
        final ArrayList<ArrayList<Long>> splittedIds = (ArrayList<ArrayList<Long>>) getIntent().getSerializableExtra("splittedIds");

        screenType = getIntent().getIntExtra("mentionType", 0);
        switch (screenType) {
            case 0:
                mentionText=  mSharedPreferences.getString("followerText"+unfApplication.getUser().getId(),null);

                break;

            case 1:
                mentionText=  mSharedPreferences.getString("followingText"+unfApplication.getUser().getId(),null);
                break;
        }

        if (mentionText != null) {
            etMention.setText(mentionText);
            etMention.setVisibility(View.GONE);
            tvMention.setText(mentionText);
            tvMention.setVisibility(View.VISIBLE);
            tvSaveEdit.setText(getString(R.string.edit));
        }
        new GetUnFollowerUser().execute(splittedIds.get(counter));
        System.out.println("array count : " + splittedIds.size());
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splittedIds.size() - 1 > counter) {
                    new GetUnFollowerUser().execute(splittedIds.get(++counter));
                }


            }
        });

        tvSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSaveEdit.getText().toString().equalsIgnoreCase(getString(R.string.save))) {
                    tvMention.setText(etMention.getText().toString());
                    etMention.setVisibility(View.GONE);
                    tvMention.setVisibility(View.VISIBLE);
                    tvSaveEdit.setText(getString(R.string.edit));
                    String value = tvMention.getText().toString();
                    switch (screenType) {
                        case 0:

                            mSharedPreferences.edit().putString("followerText"+unfApplication.getUser().getId(), value).commit();

                            break;

                        case 1:
                            mSharedPreferences.edit().putString("followingText"+unfApplication.getUser().getId(), value).commit();
                            break;
                    }
                }
                else{
                    tvMention.setText("");
                    tvSaveEdit.setText(getString(R.string.save));
                    etMention.setVisibility(View.VISIBLE);
                    tvMention.setVisibility(View.GONE);

                }



            }
        });
    }

    class GetUnFollowerUser extends AsyncTask<ArrayList<Long>, ResponseList<User>, ResponseList<User>> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MakeMentionActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
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
            } catch (Exception e1) {
                e1.printStackTrace();
                Toast.makeText(MakeMentionActivity.this, R.string.shoult_login, Toast.LENGTH_SHORT).show();
                finish();
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
            pDialog.dismiss();
            usersAll.addAll((ArrayList<User>) users);
            unfListAdapter.setUsers(usersAll);
            unfListAdapter.notifyDataSetChanged();
            for (User l : users) {
                System.out.println(friendCount++ + "friend name : " + l.getName());
            }
            // updating UI from Background Thread
        }

        public long[] convertIntegers(List<Long> longs) {
            long[] ret = new long[longs.size()];
            Iterator<Long> iterator = longs.iterator();
            for (int i = 0; i < ret.length; i++) {
                ret[i] = iterator.next().longValue();
            }
            return ret;
        }

    }

    public void mention(User user) {
        if (tvMention.getText().toString().length()>0&&(tvMention.getText().toString().length()+user.getScreenName().length())<140) {
            new MentionReq().execute(user);

        }
        else if(tvMention.getText().toString().length()==0){
            Toast.makeText(this,getString(R.string.enter_text),Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,getString(R.string.short_of_140),Toast.LENGTH_SHORT).show();

        }


    }

    class MentionReq extends AsyncTask<User, User, User> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MakeMentionActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected User doInBackground(User... args) {

            User user = args[0];
            try {
                    twitter.updateStatus("@" + args[0].getScreenName() + " " + tvMention.getText().toString());


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
        protected void onPostExecute(User user) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (user != null) {
                usersAll.remove(user);
                unfListAdapter.setUsers(usersAll);
                unfListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MakeMentionActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
            }


            // updating UI from Background Thread
        }

    }

}
