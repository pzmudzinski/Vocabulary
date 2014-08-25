package com.pz.vocabulary.app.screens.lists;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.screens.VocabularyFragment;
import com.pz.vocabulary.app.screens.WordDetailsActivity;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.utils.DictionaryUtils;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_word)
public class WordsListFragment extends VocabularyFragment implements AbsListView.OnItemClickListener {

    private ListView mListView;

    private ListAdapter mAdapter;

    private List<Word> words;

    public WordsListFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            setWordsFromBundle(getArguments());
        }
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWordsFromBundle(Bundle bundle)
    {
        this.words  = DictionaryUtils.getWordsFromBundle(getDictionary(), bundle);

        mAdapter = new ArrayAdapter<Word>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, words);
        if (mListView != null)
            mListView.setAdapter(mAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);

        if (getArguments() != null )
        {
            int titleID = getArguments().getInt(Arguments.ARG_LIST_TITLE);

            setListTitle(titleID);
            mListView.setAdapter(mAdapter);
        }

        mListView.setOnItemClickListener(this);

        return view;
    }

    public void setListTitle(int titleID)
    {
        String listTitle = null;
        if (titleID != 0)
            listTitle = getString(titleID);

        if (!TextUtils.isEmpty(listTitle)) {
            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(25);
            textView.setClickable(false);
            textView.setText(listTitle);
            mListView.addHeaderView(textView);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Word word = (Word) mListView.getItemAtPosition(position);
            if (word == null)
                return;
            WordDetailsActivity.open(getActivity(), word.getId());
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public ListView getListView() {
        return mListView;
    }
}
