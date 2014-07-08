package com.pz.vocabulary.app;

import android.app.Application;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDatabaseHelper;
import com.pz.vocabulary.app.utils.Logger;

import org.androidannotations.annotations.EApplication;

import java.util.Locale;

/**
 * Created by piotr on 05/06/14.
 */
@EApplication
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log("app", "onCreate()");
        Locale.setDefault(new Locale("pl"));
        //getApplicationContext().deleteDatabase(getString(R.string.database_name));
        OpenHelperManager.getHelper(this, OrmLiteSQLDatabaseHelper.class);
    }

}
