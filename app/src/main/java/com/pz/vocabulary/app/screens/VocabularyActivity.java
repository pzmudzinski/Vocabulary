package com.pz.vocabulary.app.screens;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.pz.vocabulary.app.App;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDatabaseHelper;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDictionary;

/**
 * Created by piotr on 06/06/14.
 */
public class VocabularyActivity extends ActionBarActivity implements DictionaryProvider {

    private Dictionary dictionary;

    protected App getApp()
    {
        return (App)getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
