package com.umutkina.findunfollowersapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umutkina.findunfollowersapp.ClearTweetActivity;
import com.umutkina.findunfollowersapp.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.Status;

/**
 * Created by mac on 23/05/15.
 */
public class ClearTweetAdapter extends BaseAdapter {

    Context context;

    ArrayList<Status> statuses= new ArrayList<>();


    public ClearTweetAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return statuses.size();
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
            convertView = vi.inflate(R.layout.listview_followback, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Status status = statuses.get(position);

        Picasso.with(context).load(status.getUser().getBiggerProfileImageURL()).into(holder.ivUserImage);
        holder.tvUserName.setText(position + 1 + " : " + status.getUser().getName()+" = "+status.getText());
        holder.tvUserFollowBack.setText(context.getString(R.string.Delete));
       
        holder.tvUserFollowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearTweetActivity clearTweetActivity = (ClearTweetActivity) context;
                clearTweetActivity.removeTweet(status.getId());
            }
        });


        return convertView;
    }


    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'listview_unflist.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.iv_user_image)
        ImageView ivUserImage;
        @InjectView(R.id.tv_user_name)
        TextView tvUserName;
        @InjectView(R.id.tv_user_followback)
        TextView tvUserFollowBack;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}