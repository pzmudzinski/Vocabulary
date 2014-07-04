package com.pz.vocabulary.app.screens;

import android.support.v4.app.Fragment;

import com.pz.vocabulary.app.App;
import com.pz.vocabulary.app.sql.Dictionary;

/**
 * Created by piotr on 07/05/14.
 */
public class VocabularyFragment extends Fragment implements DictionaryProvider {

    protected App getApp()
    {
        return (App) getActivity().getApplicationContext();
    }

    @Override
    public Dictionary getDictionary()
    {
        return ((DictionaryProvider)getActivity()).getDictionary();
    }
}
