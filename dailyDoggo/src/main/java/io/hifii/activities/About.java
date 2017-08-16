package io.hifii.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.facebook.common.logging.FLog;

import io.hifii.HiFii;
import io.hifii.R;

/**
 * Created by kyler on 10/6/15.
 */
public class About extends HiFii {

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_ABOUT;
    }

    @Override
    protected Context getContext() {
        return About.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        boolean shouldBeFloatingWindow = false;
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        //  Fresco.initialize(getApplicationContext());
        setContentView(R.layout.about);
        getSupportActionBar().setTitle("HiFii");
    }

}