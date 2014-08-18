package com.pz.vocabulary.app.screens.lists;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.pz.vocabulary.app.App;
import com.pz.vocabulary.app.screens.DictionaryProvider;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDatabaseHelper;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDictionary;

/**
 * Created by piotr on 29/06/14.
 */
public class VocabularyListActivity extends ListActivity implements DictionaryProvider {

    private Dictionary dictionary;

    protected App getApp()
    {
        return (App)getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dictionary getDictionary()
    {
        if (dictionary == null)
        {
            OrmLiteSQLDatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, OrmLiteSQLDatabaseHelper.class);
            this.dictionary = new OrmLiteSQLDictionary(this, databaseHelper);
        }
        return dictionary;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dictionary != null)
            dictionary.close();

    }
}
