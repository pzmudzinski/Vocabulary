package com.pz.vocabulary.app.screens;


import android.support.v4.app.FragmentActivity;

import com.pz.vocabulary.app.App;
import com.pz.vocabulary.app.sql.Dictionary;

/**
 * Created by piotr on 06/06/14.
 */
public class VocabularyActivity extends FragmentActivity {
    protected App getApp()
    {
        return (App)getApplicationContext();
    }

    protected Dictionary getDictionary()
    {
        return getApp().getDictionary();
    }
}
