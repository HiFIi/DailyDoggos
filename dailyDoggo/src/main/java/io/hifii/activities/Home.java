package io.hifii.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.RelativeLayout;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.chuross.AspectRatioImageView;
import com.google.samples.apps.iosched.ui.widget.CheckableFloatingActionButton;
import com.google.samples.apps.iosched.ui.widget.ObservableScrollView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.hifii.HiFii;
import io.hifii.R;
import io.hifii.utils.UIUtils;

/**
 * Created by kyler on 10/26/15.
 */
public class Home extends HiFii implements ObservableScrollView.Callbacks {
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
    private SimpleDraweeView draweeView;
    private int mPhotoHeightPixels;
    private View mAddScheduleButtonContainer;
    private CheckableFloatingActionButton mAddScheduleButton;
    private int mAddScheduleButtonContainerHeightPixels;
    private View mScrollViewChild;
    private int mHeaderHeightPixels;
    private View mDetailsContainer;
    private ObservableScrollView mScrollView;
    private View mPhotoViewContainer;
    private boolean mHasPhoto;
    private float mMaxHeaderElevation;
    private Handler mHandler;
    private float mFABElevation;
    //  private static String draweeUrlString;
    private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            recomputePhotoAndScrollingMetrics();
        }
    };


    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_HOME;
    }

    @Override
    protected Context getContext() {
        return Home.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(getApplicationContext());
        boolean shouldBeFloatingWindow = false;
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        setTheme(R.style.Theme_HiFii_Home);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        super.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // I should perhaps do some research to see whether or not
        // setting the SupportActionBars title to an empty string is
        // more efficient than *.setTitle(null);

        getSupportActionBar().setTitle(null);

        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        try {
            date = format1.parse("12-10-2012");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(format2.format(date));

        String urlDoggo = format1.toString() + "/" + format1.toString() + ".jpg";

        //time
        SimpleDateFormat stf = new SimpleDateFormat("HHmm", Locale.ENGLISH);
        stf.format(new Date());


        Calendar calc = java.util.Calendar.getInstance();

        @SuppressLint("WrongConstant") int day = calc.get(java.util.Calendar.DATE);
        @SuppressLint("WrongConstant") int month = calc.get(Calendar.MONTH) + 1;
        @SuppressLint("WrongConstant") int year = calc.get(java.util.Calendar.YEAR);

        String currentdate = month + "-" + day + "-" + year + "/" + month + "-" + day + "-" + year + ".jpg";
        //   Toast.makeText(this, currentdate, Toast.LENGTH_LONG).show();

        Uri mDraweeUri = Uri.parse("http://dailydoggos.xyz/" + currentdate);
        Uri mDraweeUriTest = Uri.parse("http://i.imgur.com/NlCfUH1.jpg");

        draweeView = (SimpleDraweeView) findViewById(R.id.dailyDoggoImgParallax);
        draweeView.setImageURI(mDraweeUri);

        mHasPhoto = true;

        mHandler = new Handler();
        initViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScrollView == null) {
            return;
        }
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
    }

    private void initViews() {
        mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);
        mMaxHeaderElevation = getResources().getDimensionPixelSize(
                R.dimen.session_detail_max_header_elevation);
        mScrollView = findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.VISIBLE);
        mDetailsContainer = findViewById(R.id.details_container);
        mToolbar.setVisibility(View.VISIBLE);
        mPhotoViewContainer = findViewById(R.id.session_photo_container);
        draweeView = (SimpleDraweeView) findViewById(R.id.dailyDoggoImgParallax);
        mAddScheduleButtonContainer = findViewById(R.id.add_schedule_button_container);
        mAddScheduleButton = findViewById(R.id.add_schedule_button);
     //   RelativeLayout changingColorRL = findViewById(R.id.HomeRLPalette);
        displayData();
        darkenNavBar();
        //    setMidnightStatusbarColor();

    }

    /**
     * private void changeAspectRatio(int width, int height, Float ratio, PercentRelativeLayout mPercentRelativeLayout) {
     * PercentRelativeLayout.LayoutParams layoutParamsMain = new PercentRelativeLayout.LayoutParams(getApplicationContext(), null);
     * if (width != 0)
     * layoutParamsMain.width = width;
     * if (height != 0)
     * layoutParamsMain.width = width;
     * PercentLayoutHelper.PercentLayoutInfo info1 = layoutParamsMain.getPercentLayoutInfo();
     * if (ratio != 0f)
     * info1.aspectRatio = ratio;
     * mPercentRelativeLayout.setLayoutParams(layoutParamsMain);
     * mPercentRelativeLayout.requestLayout();
     * }
     * <p>
     * <p>
     * changeAspectRatio((int)(width*.9), 0, 1.77f, mPercentRelativeLayout);
     **/

    private void darkenNavBar() {
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.about_alt_navbar_color_dark));
    }

    private void recomputePhotoAndScrollingMetrics() {
     //   mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (draweeView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
            //    mDetailsContainer.setPadding(16, 150, 16, 150);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);
     //   mHeaderBox.setTranslationY(newTop);
        mAddScheduleButtonContainer.setTranslationY(newTop + mHeaderHeightPixels
                - mAddScheduleButtonContainerHeightPixels / 2);

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress = Math.min(Math.max(UIUtils.getProgress(scrollY,
                    0,
                    mPhotoHeightPixels), 0), 1);
        }

        //    ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);
   //     ViewCompat.setElevation(mAddScheduleButtonContainer, gapFillProgress * mMaxHeaderElevation
      //          + mFABElevation);
  //      ViewCompat.setElevation(mAddScheduleButton, gapFillProgress * mMaxHeaderElevation
     //           + mFABElevation);

        // The code below is to change the statusbar color from transparent to a teal
        // green color I used.
       /** int baseColor = getResources().getColor(R.color.black__10_percent);
        float alpha = (float) Math.min(0.2, (float) scrollY / mHeaderBox.getHeight());
        Window window = getWindow();
        window.setStatusBarColor(UIUtils.getColorWithAlpha(alpha, (darkenColor(baseColor)))); **/

        // ViewCompat.setElevation(draweeView, gapFillProgress * mMaxHeaderElevation);
    //    ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);
      //  ViewCompat.setElevation(mDetailsContainer, gapFillProgress * mMaxHeaderElevation);
     //   ViewCompat.setElevation(mAddScheduleButtonContainer, gapFillProgress * mMaxHeaderElevation
     //           + mFABElevation);
     //////////  ViewCompat.setElevation(mAddScheduleButton, gapFillProgress * mMaxHeaderElevation
    //            + mFABElevation);

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);

    }

    /**
     * private final void setMidnightStatusbarColor() {
     * final Integer colorFrom = getResources().getColor(R.color.perfect_dark_bg);
     * final Integer colorTo = getResources().getColor(Integer.parseInt("#FFfafafa"));
     * ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
     * colorAnimation.setDuration(200);
     * colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
     *
     * @TargetApi(Build.VERSION_CODES.LOLLIPOP)
     * @Override public void onAnimationUpdate(ValueAnimator animator) {
     * mHeaderBox.setBackgroundColor((Integer) animator.getAnimatedValue());
     * <p>
     * }
     * <p>
     * });
     * colorAnimation.start();
     * }
     **/

    private void displayData() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mScrollViewChild.setVisibility(View.VISIBLE);
            }
        });
    }
}
