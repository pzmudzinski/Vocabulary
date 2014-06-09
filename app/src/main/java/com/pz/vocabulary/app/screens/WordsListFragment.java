package com.pz.vocabulary.app.screens;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Translation;
import com.pz.vocabulary.app.models.Word;
import com.pz.vocabulary.app.utils.Arguments;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_word)
public class WordsListFragment extends VocabularyFragment implements AbsListView.OnItemClickListener {

    private ListView mListView;

    private ListAdapter mAdapter;

    public WordsListFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Word> words = new ArrayList<Word>();
        if (getArguments() == null ||  getArguments().getLong(Arguments.ARG_WORDS_FROM_LANGUAGE) == 0)
        {
            long wordID = getActivity().getIntent().getExtras().getLong(WordDetailsActivity.ARG_WORD_ID);
            List<Translation> meanings = getDictionary().findMeanings(wordID);
            for (Translation meaning : meanings)
            {
                words.add(getDictionary().findWord(meaning.getWordTo()));
            }
        } else {
            long langId = getArguments().getLong(Arguments.ARG_WORDS_FROM_LANGUAGE);
            words = getDictionary().findWords(langId);
        }

        mAdapter = new ArrayAdapter<Word>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, words);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);

        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);

        textView.setText(getString(R.string.word_title_meanings));
        mListView.addHeaderView(textView);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Word word = (Word) mListView.getAdapter().getItem(position);
            WordDetailsActivity.open(getActivity(), word.getId());
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }
}
