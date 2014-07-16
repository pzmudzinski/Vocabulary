package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.pz.vocabulary.app.MainActivity_;
import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.screens.lists.WordsListFragment;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.views.ScoreView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity(R.layout.activity_word_details)
public class WordDetailsActivity extends VocabularyActivity implements Arguments{
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
        args.putLong(SHOW_WORD_MEANINGS, wordID);

        Intent intent = WordDetailsActivity_.intent(context).get();
        intent.putExtras(args);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        wordsListFragment.setListTitle(R.string.word_title_meanings);
        wordsListFragment.setWordsFromBundle(getIntent().getExtras());
    }

    @Click(R.id.deleteButton)
    protected void onWordDelete()
    {
        askToConfirmDelete();
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

    public void askToConfirmDelete()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(
                this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getDictionary().deleteWord(word.getId());
                MainActivity_.intent(WordDetailsActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                finish();
            }
        }).setNegativeButton(android.R.string.cancel, null).create();

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.delete_word));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.delete_word_are_you_sure));

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.abc_ic_clear);

        // Showing Alert Message
        alertDialog.show();
    }
}
