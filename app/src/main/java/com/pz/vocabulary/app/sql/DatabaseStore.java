package com.pz.vocabulary.app.sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Dictionary;
import com.pz.vocabulary.app.models.Language;
import com.pz.vocabulary.app.models.Memory;
import com.pz.vocabulary.app.models.Translation;
import com.pz.vocabulary.app.models.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotr on 14/05/14.
 */
public class DatabaseStore implements Dictionary{
    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Resources resources;

    public DatabaseStore(Context context, DatabaseHelper helper)
    {
        this.databaseHelper = helper;
        this.db = helper.getWritableDatabase();
        this.resources = context.getResources();
    }

    public void close()
    {
        databaseHelper.close();
    }

    @Override
    public long insertWord(Word word)
    {
        ContentValues values = new ContentValues();
        values.put(DBColumns.SPELLING, word.getSpelling());
        values.put(DBColumns.LANGUAGE_ID, word.getLanguage());

        values.put(DBColumns.NORMALIZED_SPELLING, word.getNormalizedSpelling());
        long id = findWord(word.getLanguage(), word.getNormalizedSpelling());
        if (id == -1)
        {
            id = db.insert(DatabaseHelper.TABLE_WORDS, null, values);
        } else
        {
            id = db.update(DatabaseHelper.TABLE_WORDS, values, DBColumns.ID+"=?", new String[] { String.valueOf(id)});
        }

        word.setId(id);

        return id;
    }

    @Override
    public long findWord(long langID, String spelling)
    {
        Cursor cursor = db.query(DatabaseHelper.TABLE_WORDS, new String[] {DBColumns.ID}, DBColumns.NORMALIZED_SPELLING+"='"+spelling+"' AND " + DBColumns.LANGUAGE_ID + "="+langID, null, null, null, null);
        if (cursor.moveToFirst())
            return cursor.getLong(0);

        return -1;
    }

    @Override
    public Word findWord(long id)
    {
        Cursor cursor = db.query(DatabaseHelper.TABLE_WORDS, null, DBColumns.ID+"="+String.valueOf(id), null, null, null, null);
        if (cursor.moveToFirst())
        {
            Word word = new Word(cursor.getLong(cursor.getColumnIndex(DBColumns.ID)), cursor.getLong(cursor.getColumnIndex(DBColumns.LANGUAGE_ID)), cursor.getString(cursor.getColumnIndex(DBColumns.SPELLING)));
            return word;
        }

        return null;
    }

    public long insertMemory(Memory memory)
    {
        ContentValues values = new ContentValues();
        values.put(DBColumns.DESCRIPTION, memory.getDescription());

        long id = db.insert(DatabaseHelper.TABLE_MEMORIES, null, values);
        memory.setId(id);

        return id;
    }

    @Override
    public void addMemoryToTranslation(long memoryId, long translationId)
    {
        ContentValues values = new ContentValues();
        values.put(DBColumns.MEMORY_ID, memoryId);
        db.update(DatabaseHelper.TABLE_TRANSLATIONS, values,DBColumns.ID +" = ?", new String[] {String.valueOf(translationId)});
    }

    @Override
    public long insertTranslation(long wordFrom, long wordTo)
    {
        return insertTranslation(wordFrom, wordTo, null);
    }

    @Override
    public long insertTranslation(long wordFrom, long wordTo, Long memoryId)
    {
        ContentValues values = new ContentValues();
        values.put(DBColumns.WORD_FROM, wordFrom);
        values.put(DBColumns.WORD_TO, wordTo);
        if (memoryId != null)
            values.put(DBColumns.MEMORY_ID, memoryId);

        long id = db.insert(DatabaseHelper.TABLE_TRANSLATIONS, null, values);
        return id;
    }

    @Override
    public long insertWordsAndTranslation(Word word1, Word word2, Memory memory)
    {
        long wordId1 = insertWord(word1);
        long wordId2 = insertWord(word2);

        Long memoryId;
        if (memory != null)
            memoryId = insertMemory(memory);
        else
            memoryId = null;

        return insertTranslation(wordId1, wordId2, memoryId);
    }

    @Override
    public List<Translation> findMeanings(long wordId)
    {
        String id = Long.toString(wordId);
        List<Translation> results = new ArrayList<Translation>();
        Cursor cursor = db.rawQuery(resources.getString(R.string.query_select_meanings), new String[] { id, id , id} );
        Translation translation;
        long memoryId;
        long translationId;
        long wordToId;
        while (cursor.moveToNext())
        {
            translationId = cursor.getLong(0);
            memoryId = cursor.getLong(1);
            wordToId = cursor.getLong(2);

            translation = new Translation(translationId, wordToId, findMemory(memoryId));
            results.add(translation);
        }
        return results;
    }

    @Override
    public Memory findMemory(long memoryId)
    {
        Cursor cursor = db.query(DatabaseHelper.TABLE_MEMORIES, null, DBColumns.ID+"=?", new String[] { String.valueOf(memoryId)}, null, null, null);

        while (cursor.moveToFirst())
        {
            return new Memory(memoryId, cursor.getString(cursor.getColumnIndex(DBColumns.DESCRIPTION)));
        }

        return null;
    }

    @Override
    public List<Word> getAllWords()
    {
        List<Word> results = new ArrayList<Word>();
        Cursor query = db.query(DatabaseHelper.TABLE_WORDS, null, null, null, null, null, "language_id");
        long id;
        String spelling;
        long langId;

        while (query.moveToNext()) {
            id = query.getLong(query.getColumnIndex(DBColumns.ID));
            spelling = query.getString(query.getColumnIndex(DBColumns.SPELLING));
            langId = query.getLong(query.getColumnIndex(DBColumns.LANGUAGE_ID));

            results.add(new Word(id, langId, spelling));
        }
        query.close();
        return results;
    }

    @Override
    public Language getLanguage(long id) {
        Cursor query = db.query(DatabaseHelper.TABLE_LANGUAGES, null, DBColumns.ID+"=?", new String[] { String.valueOf(id)}, null, null, null);

        if (query.moveToFirst())
        {
            Language language = new Language(query.getLong(query.getColumnIndex(DBColumns.ID)), query.getString(query.getColumnIndex(DBColumns.NAME)));
            query.close();
            return language;
        }
        query.close();
        return null;
    }

    public List<Language> getLanguages()
    {
        Cursor query = db.query(DatabaseHelper.TABLE_LANGUAGES, null, null, null, null, null, null);

        long id;
        String name;
        List<Language> languages = new ArrayList<Language>();
        while (query.moveToNext()) {
            id = query.getLong(query.getColumnIndex(DBColumns.ID));
            name = query.getString(query.getColumnIndex(DBColumns.NAME));

            languages.add(new Language(id, name));
        }
        query.close();
        return languages;
    }
}
