package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.Arguments;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity(R.layout.activity_word_details)
public class WordDetailsActivity extends VocabularyActivity {
    public static String ARG_WORD_ID = "word_id";
    private Word word;

    @ViewById(R.id.spellingEditText)
    protected TextView editTextSpelling;
    @ViewById(R.id.languageEditText)
    protected TextView editTextLanguage;

    @FragmentById(R.id.fragment)
    protected WordsListFragment wordsListFragment;

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

    protected void bindWord()
    {
        editTextSpelling.setText(word.getSpelling());
        Language language = getDictionary().findLanguage(word.getLanguageID());
        editTextLanguage.setText(language.getName());
    }

}
