package com.pz.vocabulary.app.export.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.utils.AlertUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by piotr on 16/08/14.
 */
public class ExportTranslationsToFileTask extends AsyncTask<String, Translation, Uri>{

    private Dictionary dictionary;
    private Context context;
    private ExportListener listener;

    public interface ExportListener
    {
        public void onExportFinished(Uri uri);
        public void onExportFailed();
    }

    public ExportTranslationsToFileTask(Dictionary dictionary, Context context, ExportListener listener)
    {
        super();
        this.dictionary = dictionary;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Uri doInBackground(String... strings) {
        String fileName = strings[0];
        OutputStreamWriter outputStreamWriter = null;
        Language languageFrom = dictionary.getLanguages().get(0);
        List<Word> words = dictionary.findWords(languageFrom.getId());
        List<Translation> translations;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));

            for (Word w : words)
            {
                translations = dictionary.findMeanings(w.getId());
                for (Translation translation : translations)
                {
                    stringBuilder.append(w.getSpelling());
                    stringBuilder.append(";");
                    stringBuilder.append(translation.getTranslation().getSpelling());
                    if (translation.getMemory() != null)
                        stringBuilder.append(";").append(translation.getMemory().getDescription());
                    stringBuilder.append("\n");
                }
            }

            File root = new File(Environment.getExternalStorageDirectory(), "export");
            if (!root.exists()) {
                root.mkdirs();
            }
            File exportFile = new File(root, fileName);

            FileWriter writer = new FileWriter(exportFile);
            writer.append(stringBuilder.toString());
            writer.flush();
            writer.close();

            Uri uri = Uri.fromFile(exportFile);

            outputStreamWriter.close();
            return uri;

        } catch (Exception e)
        {
            if (listener != null)
                listener.onExportFailed();
            AlertUtils.showToastWithText(context, R.string.export_failed);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
        if (uri == null)
            return;

        if (listener != null)
            listener.onExportFinished(uri);

    }
}
