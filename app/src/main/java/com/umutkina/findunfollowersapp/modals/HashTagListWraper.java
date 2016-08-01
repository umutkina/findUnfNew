package com.umutkina.findunfollowersapp.modals;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by umutkina on 31/07/16.
 */
public class HashTagListWraper  implements Serializable{

    private static final long serialVersionUID = 6369571079509647265L;
    ArrayList<HashTag> hashTagArrayList;

    public ArrayList<HashTag> getHashTagArrayList() {
        return hashTagArrayList;
    }

    public void setHashTagArrayList(ArrayList<HashTag> hashTagArrayList) {
        this.hashTagArrayList = hashTagArrayList;
    }
}
