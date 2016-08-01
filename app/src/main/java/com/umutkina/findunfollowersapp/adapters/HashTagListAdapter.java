package com.umutkina.findunfollowersapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umutkina.findunfollowersapp.HashTagAddActivity;
import com.umutkina.findunfollowersapp.R;
import com.umutkina.findunfollowersapp.modals.HashTag;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by umutkina on 31/07/16.
 */
public class HashTagListAdapter extends BaseAdapter {


    Context context;

    ArrayList<HashTag> hashTagArrayList = new ArrayList<>();


    public HashTagListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return hashTagArrayList.size();
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

        final HashTag user = hashTagArrayList.get(position);
        holder.tvTweet.setText(user.getHashTag());

        holder.tvRemoveTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashTagArrayList.remove(user);
//notifyDataSetChanged();
//
                HashTagAddActivity addTweetListItemActivity = (HashTagAddActivity) context;
                addTweetListItemActivity.setHashTagListIttem(hashTagArrayList);
            }
        });


        return convertView;
    }

    public ArrayList<HashTag> getHashTagArrayList() {
        return hashTagArrayList;
    }

    public void setHashTagArrayList(ArrayList<HashTag> hashTagArrayList) {
        this.hashTagArrayList = hashTagArrayList;
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
