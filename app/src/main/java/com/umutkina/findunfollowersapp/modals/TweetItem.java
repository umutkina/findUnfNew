package com.umutkina.findunfollowersapp.modals;

import java.io.Serializable;

/**
 * Created by umutkina on 08/07/16.
 */
public class TweetItem implements Serializable {
    private static final long serialVersionUID = 7889620761574855973L;
    String tweet;
    boolean twitted;

    public boolean isTwitted() {
        return twitted;
    }

    public void setTwitted(boolean twitted) {
        this.twitted = twitted;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }
}
