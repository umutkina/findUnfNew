package com.umutkina.findunfollowersapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umutkina.findunfollowersapp.AddTweetListItemActivity;
import com.umutkina.findunfollowersapp.BaseActivity;
import com.umutkina.findunfollowersapp.R;
import com.umutkina.findunfollowersapp.modals.Const;
import com.umutkina.findunfollowersapp.modals.TweetList;
import com.umutkina.findunfollowersapp.modals.TweetListWrapper;
import com.umutkina.findunfollowersapp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by umutkina on 08/07/16.
 */
public class TweetListAdapter extends BaseAdapter {


    Context context;

    ArrayList<TweetList> tweetLists = new ArrayList<>();


    public TweetListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return tweetLists.size();
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
            convertView = vi.inflate(R.layout.tweet_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final TweetList tweetList = tweetLists.get(position);
        holder.tvTweetListName.setText(tweetList.getName());

        holder.tvChooseThisList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivity baseActivity= (BaseActivity) context;
                baseActivity.mSharedPreferences.edit().putString(Const.SELECTED_TWEET_NAME,tweetList.getName()).commit();
                baseActivity.finish();
            }
        });
        holder.tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTweetListItemActivity.newIntent(context,tweetList.getName());
            }
        });
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivity baseActivity= (BaseActivity) context;
                ArrayList<TweetList> tweetLists = getTweetLists();
                for (int i = 0; i < tweetLists.size(); i++) {
                    TweetList tweetList = tweetLists.get(i);
                    if (tweetList.getName().equalsIgnoreCase(tweetList.getName())) {
                        tweetLists.remove(i);

                        break;
                    }
                }

                TweetListWrapper tweetListWrapper= new TweetListWrapper();
                tweetListWrapper.setTweetLists(tweetLists);
                String json = Utils.getJson(tweetListWrapper);
                baseActivity.mSharedPreferences.edit().putString(Const.TWEET_LIST,json).commit();

                notifyDataSetChanged();

            }
        });



        return convertView;
    }

    public ArrayList<TweetList> getTweetLists() {
        return tweetLists;
    }

    public void setTweetLists(ArrayList<TweetList> tweetLists) {
        this.tweetLists = tweetLists;
    }

    static

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'listview_unflist.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

    class ViewHolder {
        @InjectView(R.id.tv_tweet_list_name)
        TextView tvTweetListName;
        @InjectView(R.id.tv_detail)
        TextView tvDetail;
        @InjectView(R.id.tv_choose_this_list)
        TextView tvChooseThisList;
        @InjectView(R.id.tv_delete)
        TextView tvDelete;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
