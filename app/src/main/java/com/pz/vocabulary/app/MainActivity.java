package com.pz.vocabulary.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.pz.vocabulary.app.export.ExportActivity_;
import com.pz.vocabulary.app.export.ImportActivity_;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.screens.SectionsPagerAdapter;
import com.pz.vocabulary.app.screens.SelectLanguageActivity_;
import com.pz.vocabulary.app.screens.SettingsActivity_;
import com.pz.vocabulary.app.screens.Updatable;
import com.pz.vocabulary.app.screens.VocabularyActionBarActivity;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.utils.CustomAlertDialogBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends VocabularyActionBarActivity implements ActionBar.TabListener, Arguments {

    SectionsPagerAdapter mSectionsPagerAdapter;

    @ViewById(R.id.pager)
    ViewPager mViewPager;

    private InterstitialAd interstitial;
    private static final String AD_ID = "ca-app-pub-8907084247556169/8134071136";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        if (!getDictionary().hasItems(Language.class)) {
            SelectLanguageActivity_.intent(this).start();
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(AD_ID);

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                AdRequest adRequest = new AdRequest.Builder().addTestDevice(AD_ID).build();
                interstitial.loadAd(adRequest);
            }
        });
        // actionBar.setHomeButtonEnabled(false);
    }

    @AfterViews
    protected void init() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        String[] titles = getResources().getStringArray(R.array.tab_titles);
        mSectionsPagerAdapter = new SectionsPagerAdapter(titles, getSupportFragmentManager());

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
        } else if (id == R.id.action_export) {
            ExportActivity_.intent(this).start();
        } else if (id == R.id.action_import) {
            ImportActivity_.intent(this).start();
        } else if (id == android.R.id.home) {
            return true;
        } else if (id == R.id.action_about) {
            final TextView textView = new TextView(this);
            Dialog dialog = new Dialog(this, R.style.CustomDialog);
            textView.setText(R.string.about_text);
            textView.setTextSize(16);
            textView.setTextColor(getResources().getColor(R.color.text_color));
            int padding = getResources().getDimensionPixelSize(R.dimen.base_padding);
            textView.setPadding(padding, padding, padding, padding);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            dialog.setTitle(R.string.about);
            dialog.setContentView(textView);

            dialog.show();


        } else if (id == R.id.action_support) {
            displayInterstitial();
        }
        return super.onOptionsItemSelected(item);
    }

    // Invoke displayInterstitial() when you are ready to display an interstitial.
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            Toast.makeText(this, R.string.click_on_ad_please, Toast.LENGTH_LONG).show();
            interstitial.show();
        }
    }

    @OnActivityResult(Arguments.ARG_REQUEST_SETTIGNS)
    protected void onDestroyIntent(int resultCode) {
        if (resultCode == INTENT_RESULT_DELETE) {
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
