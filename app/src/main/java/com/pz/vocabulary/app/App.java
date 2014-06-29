package com.pz.vocabulary.app;

import android.app.Application;

import com.pz.vocabulary.app.sql.core_sql.CoreSQLDatabaseHelper;
import com.pz.vocabulary.app.sql.core_sql.CoreSQLDictionary;
import com.pz.vocabulary.app.sql.Dictionary;

import org.androidannotations.annotations.EApplication;

/**
 * Created by piotr on 05/06/14.
 */
@EApplication
public class App extends Application {

    private Dictionary dictionary;

    @Override
    public void onCreate() {
        super.onCreate();
        initDictionary();
    }

    public void initDictionary()
    {
        CoreSQLDatabaseHelper helper = new CoreSQLDatabaseHelper(this, getString(R.string.database_name));
        this.dictionary = new CoreSQLDictionary(this, helper);
    }

    public Dictionary getDictionary()
    {
        return dictionary;
    }
}
