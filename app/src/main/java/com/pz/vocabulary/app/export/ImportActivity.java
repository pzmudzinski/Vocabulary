package com.pz.vocabulary.app.export;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.export.tasks.AddTranslationsToDBTask;
import com.pz.vocabulary.app.export.tasks.ReadTranslationsAsyncTask;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.util.List;

/**
 * Created by piotr on 10/08/14.
 */
@EActivity(R.layout.activity_import)
public class ImportActivity extends ShareVocabularyActivity implements ReadTranslationsAsyncTask.ReadTranslationsListener, AddTranslationsToDBTask.AddTranslationsToDBListener {
    FileDialog fileDialog;
    private static final int REQUEST_CHOOSER = 1234;
    private ReadTranslationsAsyncTask readTranslationsAsyncTask;
    private AddTranslationsToDBTask addTranslationsToDBTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Click(R.id.button)
    protected void onButtonClicked()
    {
        // Create the ACTION_GET_CONTENT Intent

        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("text/plain");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {

                    final Uri uri = data.getData();

                    clearConsole();
                    readTranslationsAsyncTask = new ReadTranslationsAsyncTask(this, this);

                    showProgressBar();
                    readTranslationsAsyncTask.execute(uri);
                }
                break;
        }
    }

    @Override
    public void onSuccess(List<ReadTranslationsAsyncTask.RawTranslation> translations) {
        String nParsedTranslations = getResources().getQuantityString(R.plurals.import_parsed_n_translations, translations.size(), translations.size());
        appendToConsole(nParsedTranslations);

        startAddingToDB(translations);
    }

    @Override
    public void onError(int errorCode) {
        hideProgressBar();
        appendToConsole(R.string.import_failed);

        if (errorCode == ReadTranslationsAsyncTask.ERROR_CANNOT_READ_FILE)
            appendToConsole(R.string.error_while_reading_file);
        else if (errorCode == ReadTranslationsAsyncTask.ERROR_HAVENT_FOUND_ANY_TRANSLATIONS)
            appendToConsole(R.string.error_translations_not_found);
    }

    private void startAddingToDB(List<ReadTranslationsAsyncTask.RawTranslation> translations)
    {
        this.addTranslationsToDBTask = new AddTranslationsToDBTask(getDictionary(), this);
        addTranslationsToDBTask.execute(translations);
        appendToConsole(R.string.import_start_adding_to_db);

    }

    @Override
    public void onEnd() {
        appendToConsole(R.string.import_adding_to_db_finished);
        hideProgressBar();
    }

    @Override
    public void beforeAddingTranslation(ReadTranslationsAsyncTask.RawTranslation translation) {
        String translationText = String.format(getString(R.string.import_adding_translation), translation.from, translation.to);
        appendToConsole(translationText);
    }
}
