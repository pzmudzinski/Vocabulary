package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.views.ScoreView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity(R.layout.activity_word_details)
public class WordDetailsActivity extends VocabularyActivity {
    public static String ARG_WORD_ID = "word_id";
    private Word word;

    @ViewById(R.id.spellingEditText)
    protected TextView textViewSpelling;
    @ViewById(R.id.languageTextView)
    protected TextView textViewLanguage;
    @ViewById(R.id.insertionTextView)
    protected TextView insertionDateTextView;

    @FragmentById(R.id.fragment)
    protected WordsListFragment wordsListFragment;

    @ViewById(R.id.scoreView)
    protected ScoreView scoreView;

    public static void open(Context context, long wordID)
    {
        Bundle args = new Bundle();
        args.putLong(ARG_WORD_ID, wordID);
        args.putLong(Arguments.ARG_WORD_MEANINGS, wordID);

        Intent intent = WordDetailsActivity_.intent(context).get();
        intent.putExtras(args);

        context.startActivity(intent);
    }

    @AfterViews
    protected void init()
    {
        long wordID = getIntent().getExtras().getLong(ARG_WORD_ID);
        this.word = getDictionary().findWord(wordID);
        if (word == null)
            throw new NullPointerException("null word for " + wordID);

        bindWord();

        wordsListFragment.setEmptyText(getString(R.string.word_empty_meanings));
    }

    SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");

    protected void bindWord()
    {
        textViewSpelling.setText(word.getSpelling());
        Language language = getDictionary().findLanguage(word.getLanguageID());
        textViewLanguage.setText(String.format(getString(R.string.language),language.getName().toUpperCase()));
        float acquaintance = getDictionary().getWordAcquaintance(word.getId());
        scoreView.display(acquaintance);
        Date insertionDate = getDictionary().getInsertionDate(word.getId());
        insertionDateTextView.setText(String.format(getString(R.string.word_insertion_date), dateFormat.format(insertionDate)));
    }

}
