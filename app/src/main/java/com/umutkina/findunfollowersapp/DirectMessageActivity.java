package com.umutkina.findunfollowersapp;

import android.os.Bundle;

import java.util.ArrayList;


public class DirectMessageActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);
        final ArrayList<ArrayList<Long>> splittedIds = (ArrayList<ArrayList<Long>>) getIntent().getSerializableExtra("splittedIds");

    }



}
