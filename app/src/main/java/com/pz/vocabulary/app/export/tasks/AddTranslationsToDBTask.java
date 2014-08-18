package com.pz.vocabulary.app.export.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.j256.ormlite.stmt.query.Raw;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.List;

/**
 * Created by piotr on 16/08/14.
 */
public class AddTranslationsToDBTask extends AsyncTask<List<ReadTranslationsAsyncTask.RawTranslation>, ReadTranslationsAsyncTask.RawTranslation, Void> {

    private Dictionary dictionary;
    private Language from;
    private Language to;

    public interface AddTranslationsToDBListener {
        public void onEnd();

        public void beforeAddingTranslation(ReadTranslationsAsyncTask.RawTranslation translation);
    }

    private AddTranslationsToDBListener listener;

    public AddTranslationsToDBTask(Dictionary dictionary, AddTranslationsToDBListener listener) {
        super();
        this.dictionary = dictionary;
        this.listener = listener;
        List<Language> languages = dictionary.getLanguages();
        this.from = languages.get(0);
        this.to = languages.get(1);
    }

    @Override
    protected Void doInBackground(List<ReadTranslationsAsyncTask.RawTranslation>... lists) {
        List<ReadTranslationsAsyncTask.RawTranslation> translations = lists[0];

        Memory memory;

        for (ReadTranslationsAsyncTask.RawTranslation translation : translations) {
            publishProgress(translation);

            if (!TextUtils.isEmpty(translation.memory))
                memory = new Memory(translation.memory);
            else
                memory = null;

            dictionary.insertWordsAndTranslation(from.newWord(translation.from), to.newWord(translation.to), memory);
        };
        return null;
    }

    @Override
    protected void onProgressUpdate(ReadTranslationsAsyncTask.RawTranslation... values) {
        super.onProgressUpdate(values);
        if (listener != null)
            listener.beforeAddingTranslation(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (listener != null)
            listener.onEnd();
    }
}
