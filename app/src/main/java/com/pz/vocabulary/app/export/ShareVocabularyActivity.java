package com.pz.vocabulary.app.export;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.screens.VocabularyActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by piotr on 10/08/14.
 */
@EActivity
public class ShareVocabularyActivity extends VocabularyActivity{

    protected static final String  TAG = "share";

    @ViewById(R.id.progressBar)
    protected ProgressBar progressBar;

    @ViewById(R.id.consoleTextView)
    protected TextView consoleTextView;

    @ViewById(R.id.scrollView)
    protected ScrollView scrollView;

    @ViewById(R.id.button)
    protected Button button;

    @UiThread
    protected void showProgressBar()
    {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        if (button != null)
            button.setEnabled(false);
    }

    @UiThread
    protected void hideProgressBar()
    {
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        if (button != null)
            button.setEnabled(true);
    }

    @UiThread
    protected void appendToConsole(int resID)
    {
        appendToConsole(getString(resID) + "\n");
    }

    @UiThread
    protected void appendToConsole(String txt)
    {
        StringBuilder stringBuilder = new StringBuilder(consoleTextView.getText());
        stringBuilder.append(txt);
        stringBuilder.append("\n");
        consoleTextView.setText(stringBuilder);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
    @UiThread
    protected void clearConsole()
    {
        consoleTextView.setText("");
    }
}
