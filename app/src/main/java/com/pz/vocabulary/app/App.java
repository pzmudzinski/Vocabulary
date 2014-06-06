package com.pz.vocabulary.app;

import android.app.Application;

import com.pz.vocabulary.app.models.Dictionary;
import com.pz.vocabulary.app.sql.DatabaseHelper;
import com.pz.vocabulary.app.sql.DatabaseStore;

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
        DatabaseHelper helper = new DatabaseHelper(this, getString(R.string.database_name));
        this.dictionary = new DatabaseStore(this, helper);
    }

    public Dictionary getDictionary()
    {
        return dictionary;
    }
}
