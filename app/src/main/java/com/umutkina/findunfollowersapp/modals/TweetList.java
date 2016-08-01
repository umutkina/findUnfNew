package com.umutkina.findunfollowersapp.modals;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by umutkina on 08/07/16.
 */
public class TweetList implements Serializable{
    private static final long serialVersionUID = -441754131296241345L;
    String name;
    ArrayList<TweetItem> tweetItems;

    public ArrayList<TweetItem> getTweetItems() {
        return tweetItems;
    }

    public void setTweetItems(ArrayList<TweetItem> tweetItems) {
        this.tweetItems = tweetItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
