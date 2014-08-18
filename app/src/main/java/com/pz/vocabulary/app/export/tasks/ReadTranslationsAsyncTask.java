package com.pz.vocabulary.app.export.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotr on 16/08/14.
 */
public class ReadTranslationsAsyncTask extends AsyncTask<Uri, Integer, List<ReadTranslationsAsyncTask.RawTranslation>>
{

    private Context context;

    public static class RawTranslation
    {
        public String from;
        public String to;
        public String memory;

        public RawTranslation(String f, String t)
        {
            this.from = f;
            this.to =t;
        }

        public RawTranslation(String f, String t, String m)
        {
            this(f,t);
            this.memory = memory;
        }
    }

    public interface ReadTranslationsListener
    {
        public void onSuccess(List<RawTranslation> translations);
        public void onError(int errorCode);
    }

    private ReadTranslationsListener listener;

    public static final int ERROR_CANNOT_READ_FILE = 1;
    public static final int ERROR_HAVENT_FOUND_ANY_TRANSLATIONS = 2;

    private int errorCode = -1;


    public ReadTranslationsAsyncTask(Context context, ReadTranslationsListener listener)
    {
        super();
        this.context = context;
        this.listener = listener;
    }
    @Override
    protected List<RawTranslation> doInBackground(Uri... uris) {
        Uri uri = uris[0];
        File file = FileUtils.getFile(context, uri);

        try {
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

            List<RawTranslation> translations = new ArrayList<RawTranslation>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (!validate(parts))
                    continue;

                String memory = parts.length < 3? null : parts[2];

                translations.add(new RawTranslation(parts[0], parts[1], memory));
            }
            reader.close();
            fileInputStream.close();

            return translations;
        } catch(Exception e)
        {
            listener.onError(ERROR_CANNOT_READ_FILE);
            this.errorCode = ERROR_CANNOT_READ_FILE;
        }

        return null;
    }

    private boolean validate(String[] lineParts)
    {
        if (lineParts.length < 2)
            return false;

        if (TextUtils.isEmpty(lineParts[0].trim()) || TextUtils.isEmpty(lineParts[1].trim()))
            return false;

        return true;
    }

    @Override
    protected void onPostExecute(List<RawTranslation> translationList) {
        super.onPostExecute(translationList);

        if (errorCode != -1)
            return;

        if (translationList == null || translationList.size() == 0)
            listener.onError(ERROR_HAVENT_FOUND_ANY_TRANSLATIONS);
        else
            listener.onSuccess(translationList);
    }

}
