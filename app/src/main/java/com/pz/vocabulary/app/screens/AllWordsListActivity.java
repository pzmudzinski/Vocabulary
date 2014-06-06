package com.pz.vocabulary.app.screens;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pz.vocabulary.app.App;
import com.pz.vocabulary.app.models.Dictionary;
import com.pz.vocabulary.app.models.Word;

import org.androidannotations.annotations.EActivity;

import java.util.List;

/**
 * Created by piotr on 05/06/14.
 */
@EActivity
public class AllWordsListActivity extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayWords();
    }

    private void displayWords()
    {
        App app = (App) getApplicationContext();
        Dictionary dictionary = app.getDictionary();

        List<Word> words = dictionary.getAllWords();

        TextView tView = new TextView(this);
        tView.setText("Words number : " + words.size());
        getListView().addHeaderView(tView);

        setListAdapter(new ArrayAdapter<Word>(this,
                android.R.layout.simple_list_item_1, words));
    }
}
