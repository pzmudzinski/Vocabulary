package com.pz.vocabulary.app.export;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.export.tasks.ExportTranslationsToFileTask;
import com.pz.vocabulary.app.utils.DateUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.io.File;

/**
 * Created by piotr on 10/08/14.
 */
@EActivity(R.layout.activity_export)
public class ExportActivity extends ShareVocabularyActivity implements ExportTranslationsToFileTask.ExportListener {

    protected ExportTranslationsToFileTask exportTask;

    protected final String EXPORT_FILE_FORMAT = "export-%d.txt";

    @Click(R.id.button)
    protected void onButtonClicked(View button)
    {
        this.exportTask = new ExportTranslationsToFileTask(getDictionary(), this, this);
        String fileName = String.format(EXPORT_FILE_FORMAT, DateUtils.today().getTime());
        exportTask.execute(fileName);
        showProgressBar();

    }

    @Override
    public void onExportFinished(Uri uri) {
        String exportTxt = String.format(getString(R.string.export_succedd), uri.toString());
        appendToConsole(exportTxt);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.send)));
        hideProgressBar();
    }

    @Override
    public void onExportFailed() {
        appendToConsole(R.string.export_failed);
        hideProgressBar();
    }
}





