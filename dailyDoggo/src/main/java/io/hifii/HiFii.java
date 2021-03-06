package io.hifii;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.samples.apps.iosched.ui.widget.ScrimInsetsScrollView;

import java.util.ArrayList;
import java.util.Calendar;

import io.hifii.activities.About;
import io.hifii.activities.Home;
import io.hifii.activities.PastPhotos;
import io.hifii.ui.HiFiiTextView;
import io.hifii.utils.LUtils;
import io.hifii.utils.RecentTasksStyler;
import io.hifii.utils.UIUtils;

public abstract class HiFii extends AppCompatActivity {
    // symbols for navdrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int NAVDRAWER_ITEM_HOME = 0;
    protected static final int NAVDRAWER_ITEM_PAST_PHOTOS = 1;
    protected static final int NAVDRAWER_ITEM_ABOUT = 3;
    private static final String TAG = "HiFii";
    public static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    private static final int NAVDRAWER_ITEM_SETTINGS = 2;
    private static final int NAVDRAWER_ITEM_INVALID = -1;
    private static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    private static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;
    // private static final int zero = 0;
    /**
     * TO DO:
     * Play with the launch delay values some more.
     */
    private static final int NAVDRAWER_CLOSE_PRELAUNCH = 300;
    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    private static final int POST_LAUNCH_FADE = 300;
    /**
     * END TO-DO
     **/

    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 400;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 800;
    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.home,
            R.string.unknown,
            R.string.settings,
            R.string.about
    };
    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_home,
            R.drawable.ic_bug_report,
            0,
            0
    };
    private static final boolean isDebug = false;
    private static String mVersionNumber;
    // list of navdrawer items that were actually added to the navdrawer, in order
    private final ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();
    // Primary toolbar and drawer toggle
    // public Toolbar mActionBarToolbar;
    public int drawerColorCalendar = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    protected Toolbar mToolbar;
    SharedPreferences pref;
    private DrawerLayout mDrawerLayout;
    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mDeferredOnDrawerClosedRunnable;
    private CharSequence mTitle;
    private Context context;
    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;
    // Helper methods for L APIs
    private LUtils mLUtils;
    // What nav drawer item should be selected?
    private int selfItem = getSelfNavDrawerItem();
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences.Editor editor;
    private HiFiiTextView userName;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecentTasksStyler.styleRecentTasksEntry(this);

        SharedPreferences first = PreferenceManager
                .getDefaultSharedPreferences(this);

        mVersionNumber = getResources().getString(R.string.version_number);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            super.finish();
        }

        if (!first.getBoolean("firstTimeRan", false)) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                 /*   if (!isDebug) {
                        Intent intent = new Intent(HiFii.this, Start.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                    } */
                }
            });

            SharedPreferences.Editor editor = first.edit();

            editor.putBoolean("firstTimeRan", true);
            editor.commit();

        }

        if (getIntent().getBooleanExtra("EXIT", false)) {
            super.finish();
        }
        Handler mHandler = new Handler();

        // Enable or disable each Activity depending on the form factor. This is necessary
        // because this app uses many implicit intents where we don't name the exact Activity
        // in the Intent, so there should only be one enabled Activity that handles each
        // Intent in the app.
        UIUtils.enableDisableActivitiesByFormFactor(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();
        int toolbarHeight = 0;
        TypedValue tv = new TypedValue();

        toolbarHeight = (int) (tv.getDimension(getResources().getDisplayMetrics()) / getResources().getDisplayMetrics().density);
        int drawerMaxWidth = (int) (getResources().getDimension(R.dimen.nav_drawer_max_width) / getResources().getDisplayMetrics().density);
        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        int drawerWidth = screenWidthDp - toolbarHeight;

        mDrawerLayout = findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.transparent));
        ScrimInsetsScrollView navDrawer = mDrawerLayout.findViewById(R.id.navdrawer);

        ViewGroup.LayoutParams layout_description = navDrawer.getLayoutParams();
        layout_description.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                drawerWidth < drawerMaxWidth ? drawerWidth : drawerMaxWidth,
                getResources().getDisplayMetrics());
        navDrawer.setLayoutParams(layout_description);

        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            mDrawerLayout = null;
            return;
        }

        final View chosenAccountContentView = findViewById(R.id.chosen_account_content_view);
        final View chosenAccountView = findViewById(R.id.chosen_account_view);

        final int navDrawerChosenAccountHeight = getResources().getDimensionPixelSize(
                R.dimen.navdrawer_chosen_account_height);
        navDrawer.setOnInsetsCallback(new ScrimInsetsScrollView.OnInsetsCallback() {
            @Override
            public void onInsetsChanged(Rect insets) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                        chosenAccountContentView.getLayoutParams();
                lp.topMargin = insets.top;
                chosenAccountContentView.setLayoutParams(lp);

                ViewGroup.LayoutParams lp2 = chosenAccountView.getLayoutParams();
                chosenAccountView.setLayoutParams(lp2);
            }
        });

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_drawer_white);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                if (mDeferredOnDrawerClosedRunnable != null) {
                    mDeferredOnDrawerClosedRunnable.run();
                    mDeferredOnDrawerClosedRunnable = null;
                }
                onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        //    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    // Subclasses can override this for custom behavior
    private void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {

    }

    private void onNavDrawerSlide(float offset) {
    }

    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Populates the navigation drawer with the appropriate items.
     */
    private void populateNavDrawer() {
        mNavDrawerItems.clear();

        mNavDrawerItems.add(NAVDRAWER_ITEM_HOME);
        mNavDrawerItems.add(NAVDRAWER_ITEM_PAST_PHOTOS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ABOUT);

        /** decide which items will appear in the nav drawer
        if (AccountUtils.hasActiveAccount(this)) {
            // Only logged-in users can save sessions, so if there is no active account,
            // there is no My Schedule
            mNavDrawerItems.add(NAVDRAWER_ITEM_MY_SCHEDULE);
        } else {
            // If no active account, show Sign In
            mNavDrawerItems.add(NAVDRAWER_ITEM_SIGN_IN);
        }

        // Explore is always shown
        mNavDrawerItems.add(NAVDRAWER_ITEM_EXPLORE);

        // If the attendee is on-site, show Map on the nav drawer
        if (attendeeAtVenue) {
            mNavDrawerItems.add(NAVDRAWER_ITEM_MAP);

        // If the experts directory hasn't expired, show it
        if (!Config.hasExpertsDirectoryExpired()) {
            mNavDrawerItems.add(NAVDRAWER_ITEM_EXPERTS_DIRECTORY);
        }

        } **/

        createNavDrawerItems();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void createNavDrawerItems() {
        ViewGroup mDrawerItemsListContainer = findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
               /* if (itemId == NAVDRAWER_ITEM_HOME) {
                    mNavDrawerItems.add(NAVDRAWER_ITEM_CALENDAR);
                } */
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            //  (~˘▾˘)~
        }
    }

    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
            case NAVDRAWER_ITEM_HOME:
                intent = new Intent(this, Home.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                break;
            case NAVDRAWER_ITEM_PAST_PHOTOS:
                intent = new Intent(this, PastPhotos.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            case NAVDRAWER_ITEM_ABOUT:
                intent = new Intent(this, About.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }, NAVDRAWER_CLOSE_PRELAUNCH);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {

            mDrawerLayout.closeDrawer(GravityCompat.START);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // change the active item on the list so the user can see the item changed
                    setSelectedNavDrawerItem(itemId);

                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            //    goToNavDrawerItem(itemId);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // fade out the main content
                    View mainContent = findViewById(R.id.main_content);
                    if (mainContent != null) {
                        mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
                    }
                }
            }, POST_LAUNCH_FADE);
        }

        //    mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //   window.setStatusBarColor(Color.BLUE);
        }

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void getActionBarToolbar() {
        if (mToolbar == null) {
            mToolbar = findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
    }

    protected int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }

        View view = getLayoutInflater().inflate(layoutToInflate, container, false);
        if (isSeparator(itemId)) {
            return view;
        }

        ImageView iconView = view.findViewById(R.id.icon);
        TextView titleView = view.findViewById(R.id.title);

        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isSeparator(int itemId) {
        return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = view.findViewById(R.id.icon);
        TextView titleView = view.findViewById(R.id.title);
        LinearLayout ll = view.findViewById(R.id.ll);

        if (selected) {
            view.setBackgroundResource(R.drawable.ll_ripple);
        }

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_item_text_color) :
                getResources().getColor(R.color.navdrawer_item_text_color));

        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.colorPrimary) :
                getResources().getColor(R.color.navdrawer_item_icon_color_inverse));
    }

    public LUtils getLUtils() {
        return mLUtils;
    }

    private boolean isSpecialItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_INVALID;
    }

    protected abstract Context getContext();
}
