package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Language;
import com.pz.vocabulary.app.models.Word;

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
    protected EditText editTextSpelling;
    @ViewById(R.id.languageEditText)
    protected EditText editTextLanguage;

    @FragmentById(R.id.fragment)
    protected WordMeaningsFragment wordMeaningsFragment;

    public static void open(Context context, long wordID)
    {
        Bundle args = new Bundle();
        args.putLong(ARG_WORD_ID, wordID);

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

        wordMeaningsFragment.setEmptyText(getString(R.string.word_empty_meanings));
    }

    protected void bindWord()
    {
        editTextSpelling.setText(word.getSpelling());
        Language language = getDictionary().findLanguage(word.getLanguageID());
        editTextLanguage.setText(language.getName());
    }

}
