package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.utils.Arguments;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity(R.layout.activity_words_by_language)
public class WordsByLanguageActivity extends VocabularyActivity {
    public static void open(Context context)
    {
        Intent intent = WordsByLanguageActivity_.intent(context).get();
        context.startActivity(intent);
    }

    @ViewById(R.id.pager)
    protected ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    protected void init()
    {
        // Instantiate a ViewPager and a PagerAdapter.
        List<Language> languages = getDictionary().getLanguages();
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), languages);
        mPager.setAdapter(mPagerAdapter);
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

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private int numberOfLanguages;
        private List<Language> languages;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Language> languageIDs) {
            super(fm);
            this.numberOfLanguages = languageIDs.size();
            this.languages = languageIDs;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            WordsListFragment wordsListFragment = WordsListFragment_.builder().build();
            Bundle bundle = new Bundle();
            bundle.putLong(Arguments.ARG_WORDS_FROM_LANGUAGE, languages.get(position).getId());
            wordsListFragment.setArguments(bundle);
            return wordsListFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return languages.get(position).getName().toUpperCase();
        }

        @Override
        public int getCount() {
            return numberOfLanguages;
        }
    }
}
