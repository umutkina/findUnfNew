package com.umutkina.findunfollowersapp.modals;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by umutkina on 08/07/16.
 */
public class TweetListWrapper implements Serializable{
    private static final long serialVersionUID = -2653423751837245210L;
    ArrayList<TweetList> tweetLists;

    public ArrayList<TweetList> getTweetLists() {
        return tweetLists;
    }

    public void setTweetLists(ArrayList<TweetList> tweetLists) {
        this.tweetLists = tweetLists;
    }
}
