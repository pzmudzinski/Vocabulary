package com.pz.vocabulary.app.screens.lists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.screens.SettingsActivity_;
import com.pz.vocabulary.app.screens.VocabularyActionBarActivity;
import com.pz.vocabulary.app.utils.Arguments;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity(R.layout.activity_words_by_language)
public class WordsTwoTabsListActivity extends VocabularyActionBarActivity implements Arguments, ActionBar.TabListener {

    @ViewById(R.id.pager)
    ViewPager mViewPager;

    public static final int TAB_COUNT = 2;

    public static void open(Context context, String[] titles, String[] types, long[] values)
    {
       Intent intent = WordsTwoTabsListActivity_.intent(context).get();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ARG_TITLES, titles);
        bundle.putStringArray(ARG_WORD_TYPES, types);
        bundle.putLongArray(ARG_WORD_IDS, values);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @ViewById(R.id.pager)
    protected ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @AfterViews
    protected void init()
    {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mPagerAdapter = new PageAdapter(
                getSupportFragmentManager(),
                getIntent().getStringArrayExtra(ARG_WORD_TYPES),
                getIntent().getLongArrayExtra(ARG_WORD_IDS)
        );
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        mViewPager.setAdapter(mPagerAdapter);

        String[] titles = getIntent().getStringArrayExtra(ARG_TITLES);

        for (int i = 0; i < TAB_COUNT; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(titles[i])
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    class PageAdapter extends FragmentPagerAdapter
    {
        private String[] types;
        private long[] values;

        public PageAdapter(FragmentManager fm, String[] types, long[] values)
        {
            super(fm);
            this.types = types;
            this.values = values;
        }

        @Override
        public Fragment getItem(int position) {
            WordsListFragment wordsListFragment = WordsListFragment_.builder().build();
            Bundle bundle = new Bundle();
            bundle.putLong(types[position], values[position]);
            wordsListFragment.setArguments(bundle);
            return wordsListFragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}
