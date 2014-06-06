package com.pz.vocabulary.app.screens;

import com.pz.vocabulary.app.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by piotr on 27.04.2014.
 */
@EFragment(R.layout.fragment_stats)
public class StatisticsFragment extends VocabularyFragment {

    @Click(R.id.buttonShowAllWords)
    protected void showAllWords()
    {
        AllWordsListActivity_.intent(getActivity()).start();
    }
}
