package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.AlertUtils;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.utils.DictionaryUtils;
import com.pz.vocabulary.app.utils.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by piotr on 27.04.2014.
 *
 */
@EFragment(R.layout.fragment_start_test)
public class StartTestFragment extends VocabularyFragment {

    @ViewById(R.id.adView)
    protected AdView adView;

    private Handler handler = new Handler();


    @AfterViews
    protected void initAd()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Arguments.AD_PREFS, Context.MODE_PRIVATE);

        if (!sharedPreferences.contains(Arguments.AD_FIRST_TIME))
        {
            sharedPreferences.edit().putBoolean(Arguments.AD_FIRST_TIME, true).commit();
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAd();
            }
        }, Arguments.AD_SHOW_DELAY);
    }

    private void loadAd()
    {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("63BD1A227D1E30162D77F0F2F520F503").build();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }
        });

        adView.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null)
            adView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null)
            adView.destroy();
    }

    @Click(R.id.buttonStartTest)
    protected void onStartTest()
    {
        List<Word> words = getDictionary().getAllWords();
        goToQuiz(words);
    }
//
    @Click(R.id.buttonStartTestWordSince)
    protected void onStartTestWordsSinceDate()
    {
        showWordsSinceDialog();
    }

    @Click(R.id.buttonStartTestToughWords)
    protected void onToughWords()
    {
        goToToughWordsQuiz();
    }
//
    public void showWordsSinceDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.start_test_words_since_title)
                .setItems(R.array.start_test_words_since_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        goToWordsSinceQuiz(which);
                    }
                });
        builder.show();
    }

    public void goToWordsSinceQuiz(int option)
    {
        Logger.log("quiz", "Selected option " + option);

        List<Word> words = DictionaryUtils.getWordSince(getDictionary(), option);
        goToQuiz(words);
    }

    public void goToQuiz(List<Word> words)
    {

        if (checkIfWordsDontExist(words))
        {

        } else {
            QuizActivity.open(getActivity(), words);
        }
    }
    private static final float MAX_SCORE_TO_BE_CLASSIFIED_AS_TOUGH = 0.8f;
//
    public void goToToughWordsQuiz()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.start_test_words_tough)
                .setItems(R.array.start_test_words_since_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        goToQuiz(DictionaryUtils.getToughWords(
                                getDictionary(),
                                which,
                                MAX_SCORE_TO_BE_CLASSIFIED_AS_TOUGH
                        ));
                    }
                });
        builder.show();
    }

    private boolean checkIfWordsDontExist(List<Word> words)
    {
        if (words == null || words.size() == 0)
        {
            AlertUtils.showEmptyTestToast(getActivity());
            return true;
        }
        else return false;
    }
}
