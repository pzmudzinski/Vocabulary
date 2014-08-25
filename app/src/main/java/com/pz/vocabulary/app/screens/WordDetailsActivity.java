package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.pz.vocabulary.app.MainActivity_;
import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.screens.lists.WordsListFragment;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.utils.CustomAlertDialogBuilder;
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
public class WordDetailsActivity extends VocabularyActivity implements Arguments, AddMeaningDialogFragment.AddMeaningDialogListener, AdapterView.OnItemLongClickListener {
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
        {
            finish();
            return;
        }


        bindWord();

        wordsListFragment.setEmptyText(getString(R.string.word_empty_meanings));
        wordsListFragment.setListTitle(R.string.word_title_meanings);
        wordsListFragment.setWordsFromBundle(getIntent().getExtras());
        wordsListFragment.getListView().setOnItemLongClickListener(this);
    }

    @Click(R.id.deleteButton)
    protected void onWordDelete()
    {
        askToConfirmDelete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_meaning)
        {

            AddMeaningDialogFragment meaningDialogFragment = AddMeaningDialogFragment.newInstance(word.getId());
            meaningDialogFragment.show(getSupportFragmentManager(), "meaning");
            return true;
        } else if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_delete_maning)
        {
            Toast.makeText(this, R.string.press_longer_to_delete_meaning, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
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
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        AlertDialog alertDialog = new CustomAlertDialogBuilder(
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

    @Override
    public void onMeaningAdded() {
        wordsListFragment.setWordsFromBundle(getIntent().getExtras());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        new AlertDialog.Builder(this);

        final int clickedIndex = i - wordsListFragment.getListView().getHeaderViewsCount();
        AlertDialog alertDialog = new CustomAlertDialogBuilder(
                this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dictionary dictionary = getDictionary();
                Word wordTo = wordsListFragment.getWords().get(clickedIndex);
                dictionary.deleteTranslation(word.getId(), wordTo.getId());
                wordsListFragment.setWordsFromBundle(getIntent().getExtras());
            }
        }).setNegativeButton(android.R.string.cancel, null).create();

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.delete_meaning));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.delete_meaning_are_you_sure));

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.abc_ic_clear);

        // Showing Alert Message
        alertDialog.show();
        return true;
    }
}
