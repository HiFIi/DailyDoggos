package io.hifii.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;

import io.hifii.R;

/**
 * Created by kyler on 11/10/16.
 */
public class Start extends Activity {
    public int mutedColor;
    public Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //   setTheme(R.style.Theme_HiFii_Home);
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.firstrun_welcome);
        super.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
