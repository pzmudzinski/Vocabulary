package com.pz.vocabulary.app.screens;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by piotr on 14/05/14.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private String[] titles;
    private static final int TAB_COUNTS = 3;

    public SectionsPagerAdapter(String[] titles, FragmentManager fm)
    {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return AddTranslationFragment_.builder().build();
            case 1:
                return StartTestFragment_.builder().build();
            case 2:
                return StatisticsFragment_.builder().build();
        }
        return null;
    }

    private Fragment mCurrentFragment;

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return TAB_COUNTS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
