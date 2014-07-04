package com.pz.vocabulary.app;

import android.app.Application;

import com.pz.vocabulary.app.utils.Logger;

import org.androidannotations.annotations.EApplication;

/**
 * Created by piotr on 05/06/14.
 */
@EApplication
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log("app", "onCreate() dziwko");
    }

}
