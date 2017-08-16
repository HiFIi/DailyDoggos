package io.hifii.activities;

/*
  Created by kyler on 12/27/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import io.hifii.HiFii;
import io.hifii.R;

public class PastPhotos extends HiFii {
    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_PAST_PHOTOS;
    }

    @Override
    protected Context getContext() {
        return PastPhotos.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_HiFii_Home);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_photos);
        super.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // I should perhaps do some research to see whether or not
        // setting the SupportActionBars title to an empty string is
        // more efficient than *.setTitle(null);

        getSupportActionBar().setTitle(null);
    }
}