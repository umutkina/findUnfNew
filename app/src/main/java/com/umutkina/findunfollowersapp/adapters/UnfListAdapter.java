package com.umutkina.findunfollowersapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umutkina.findunfollowersapp.R;
import com.umutkina.findunfollowersapp.UnfListActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import twitter4j.User;

/**
 * Created by mac on 21/04/15.
 */
public class UnfListAdapter extends BaseAdapter {


    Context context;

    ArrayList<User> users = new ArrayList<>();


    public UnfListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
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
            convertView = vi.inflate(R.layout.listview_unflist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final User user = users.get(position);
        Picasso.with(context).load(user.getBiggerProfileImageURL()).into(holder.ivUserImage);
        holder.tvUserName.setText(position+1 +" : "+user.getName());
        holder.tvUserUnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnfListActivity unfListActivity = (UnfListActivity) context;
                unfListActivity.unf(user.getId());
            }
        });


        return convertView;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
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
        @InjectView(R.id.tv_user_unf)
        TextView tvUserUnf;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}