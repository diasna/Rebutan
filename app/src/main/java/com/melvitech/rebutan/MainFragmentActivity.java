package com.melvitech.rebutan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.melvitech.rebutan.fragment.ItemListFragment;
import com.melvitech.rebutan.fragment.LeftMenuFragment;
import com.melvitech.rebutan.sync.Utils;

/**
 * Created by miku on 7/15/14.
 */
public class MainFragmentActivity extends SlidingFragmentActivity {

    protected Fragment mFrag;
    FragmentTransaction ft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_container);

        ft = getSupportFragmentManager().beginTransaction();

        initSideMenu(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("perform_updates", true) && !Utils.isAlarmSet(this)) {
            Utils.setRecurringAlarm(this);
        }

        mFrag = new ItemListFragment();
        ft.replace(R.id.container, mFrag);

        ft.commit();
    }

    private void initSideMenu(Bundle savedInstanceState) {
        setBehindContentView(R.layout.menu_frame);
        if (savedInstanceState == null) {
            mFrag = new LeftMenuFragment();
            ft.replace(R.id.menu_frame, mFrag);
        } else {
            mFrag = (Fragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
