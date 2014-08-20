package com.pz.vocabulary.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.pz.vocabulary.app.export.ExportActivity_;
import com.pz.vocabulary.app.export.ImportActivity_;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.screens.SectionsPagerAdapter;
import com.pz.vocabulary.app.screens.SelectLanguageActivity_;
import com.pz.vocabulary.app.screens.SettingsActivity_;
import com.pz.vocabulary.app.screens.Updatable;
import com.pz.vocabulary.app.screens.VocabularyActionBarActivity;
import com.pz.vocabulary.app.utils.Arguments;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends VocabularyActionBarActivity implements ActionBar.TabListener, Arguments {

    SectionsPagerAdapter mSectionsPagerAdapter;

    @ViewById(R.id.pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        if (!getDictionary().hasItems(Language.class))
        {
            SelectLanguageActivity_.intent(this).start();
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
       // actionBar.setHomeButtonEnabled(false);
    }

    @AfterViews
    protected void init()
    {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        String[] titles = getResources().getStringArray(R.array.tab_titles);
        mSectionsPagerAdapter = new SectionsPagerAdapter( titles, getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SettingsActivity_.intent(this).startForResult(Arguments.ARG_REQUEST_SETTIGNS);
            return true;
        } else if (id == R.id.action_export)
        {
            ExportActivity_.intent(this).start();
        } else if (id == R.id.action_import)
        {
            ImportActivity_.intent(this).start();
        } else if (id == android.R.id.home)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnActivityResult(Arguments.ARG_REQUEST_SETTIGNS)
    protected void onDestroyIntent(int resultCode)
    {
        if (resultCode == INTENT_RESULT_DELETE)
        {
            getDictionary().destroyEverything();
            Fragment currentFragment = mSectionsPagerAdapter.getCurrentFragment();
            if (currentFragment instanceof Updatable)
                ((Updatable) currentFragment).update();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewPager.getApplicationWindowToken(), 0);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



}
