package com.example.game.service;

import android.view.Menu;

public interface IOnBackPressed {
    void onRefresh();


    //boolean onCreateOptionsMenu(Menu menu);

    /**
     * If you return true the back press will not be taken into account, otherwise the activity will act naturally
     *
     * @return true if your processing has priority if not false
     */
    void onBackPressed();
}