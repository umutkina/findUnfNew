package com.umutkina.findunfollowersapp.modals;

import java.io.Serializable;

/**
 * Created by umutkina on 31/07/16.
 */
public class HashTag implements Serializable {
    private static final long serialVersionUID = -234101484979361457L;
    String hashTag;

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }
}
