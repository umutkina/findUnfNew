package com.umutkina.findunfollowersapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umutkina.findunfollowersapp.AddTweetListItemActivity;
import com.umutkina.findunfollowersapp.R;
import com.umutkina.findunfollowersapp.modals.TweetItem;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by umutkina on 08/07/16.
 */
public class TwweetListItemAdapter extends BaseAdapter {


    Context context;

    ArrayList<TweetItem> tweetItems = new ArrayList<>();


    public TwweetListItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return tweetItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.tweet_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final TweetItem user = tweetItems.get(position);
        holder.tvTweet.setText(user.getTweet());

        holder.tvRemoveTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweetItems.remove(user);
//notifyDataSetChanged();
//
                AddTweetListItemActivity addTweetListItemActivity = (AddTweetListItemActivity) context;
                addTweetListItemActivity.setTweetListIttem(tweetItems);
            }
        });


        return convertView;
    }

    public ArrayList<TweetItem> getTweetItems() {
        return tweetItems;
    }

    public void setTweetItems(ArrayList<TweetItem> tweetItems) {
        this.tweetItems = tweetItems;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_tweet)
        TextView tvTweet;
        @InjectView(R.id.tv_remove_tweet)
        TextView tvRemoveTweet;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
