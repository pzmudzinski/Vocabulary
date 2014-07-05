package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.AlertUtils;
import com.pz.vocabulary.app.utils.DictionaryUtils;
import com.pz.vocabulary.app.utils.Logger;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import java.util.List;

/**
 * Created by piotr on 27.04.2014.
 *
 */
@EFragment(R.layout.fragment_start_test)
public class StartTestFragment extends VocabularyFragment {

    @Click(R.id.buttonStartTest)
    protected void onStartTest()
    {
        if (checkIfWordsDontExist(getDictionary().getAllWords()))
        {

        } else {
            QuizActivity_.intent(getActivity()).showAllWords(true).start();
        }

    }

    @Click(R.id.buttonStartTestWordSince)
    protected void onStartTestWordsSinceDate()
    {
        showWordsSinceDialog();
    }

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
        if (checkIfWordsDontExist(DictionaryUtils.getWordSince(getDictionary(), option)))
        {

        } else {
            QuizActivity_.intent(getActivity()).showWordsSince(option).start();
        }
    }

    public void goToToughWordsQuiz()
    {

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
