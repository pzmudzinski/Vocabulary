package com.pz.vocabulary.app.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;

import org.androidannotations.annotations.EActivity;

import java.util.List;

/**
 * Created by piotr on 05/06/14.
 */
@EActivity
public class AllWordsListActivity extends VocabularyListActivity implements AdapterView.OnItemClickListener {

    private List<Word> words;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayWords();
    }

    private void displayWords()
    {
        Dictionary dictionary = getDictionary();

        List<Word> words = dictionary.getAllWords();
        this.words = words;

        TextView tView = new TextView(this);
        tView.setText("Words number : " + words.size());
        getListView().addHeaderView(tView);

        setListAdapter(new ArrayAdapter<Word>(this,
                android.R.layout.simple_list_item_1, words));

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Word word = words.get(i - 1 );
        WordDetailsActivity.open(this, word.getId());
    }
}
