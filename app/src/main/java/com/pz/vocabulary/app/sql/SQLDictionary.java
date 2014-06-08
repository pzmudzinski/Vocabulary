package com.pz.vocabulary.app.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Language;
import com.pz.vocabulary.app.models.Memory;
import com.pz.vocabulary.app.models.Translation;
import com.pz.vocabulary.app.models.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotr on 14/05/14.
 */
public class SQLDictionary extends SQLStore implements Dictionary{

    public SQLDictionary(Context context, DatabaseHelper helper) {
        super(context, helper);
    }

    @Override
    public long insertWord(Word word)
    {
        ContentValues values = new ContentValues();
        values.put(DBColumns.SPELLING, word.getSpelling());
        values.put(DBColumns.LANGUAGE_ID, word.getLanguageID());
        values.put(DBColumns.NORMALIZED_SPELLING, word.getNormalizedSpelling());
        long id = findWord(word.getLanguageID(), word.getNormalizedSpelling());
        if (id == -1)
        {
            id = db.insert(DatabaseHelper.TABLE_WORDS, null, values);
            word.setId(id);
        } else
        {
            db.update(DatabaseHelper.TABLE_WORDS, values, DBColumns.ID+"=?", new String[] { String.valueOf(id)});
        }

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
        Cursor cursor = getSelectQuery(DatabaseHelper.TABLE_WORDS, id);
        if (cursor.moveToFirst())
        {
            Language language = findLanguage(cursor.getLong(cursor.getColumnIndex(DBColumns.LANGUAGE_ID)));
            return Word.fromCursor(cursor, language);
        }

        return null;
    }

    public long insertMemory(Memory memory)
    {
        ContentValues values = new ContentValues();
        values.put(DBColumns.DESCRIPTION, memory.getDescription());

        long id;
        if (findMemory(memory.getDescription()) != null)
        {
            db.update(DatabaseHelper.TABLE_MEMORIES, values, DBColumns.ID +" = ?", new String[] {String.valueOf(memory.getId())});
        } else {
            id = db.insert(DatabaseHelper.TABLE_MEMORIES, null, values);
            memory.setId(id);
        }

        return memory.getId();
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

        Long memoryId = null;
        if (memory != null)
            memoryId = insertMemory(memory);

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
        Cursor cursor = getSelectQuery(DatabaseHelper.TABLE_MEMORIES, memoryId);

        while (cursor.moveToFirst())
        {
            return new Memory(memoryId, cursor.getString(cursor.getColumnIndex(DBColumns.DESCRIPTION)));
        }

        return null;
    }

    public Memory findMemory(String description)
    {
        Cursor cursor = getSelectQuery(DatabaseHelper.TABLE_MEMORIES, DBColumns.DESCRIPTION, description);

        while (cursor.moveToFirst())
        {
            return new Memory(cursor.getLong(cursor.getColumnIndex(DBColumns.ID)), cursor.getString(cursor.getColumnIndex(DBColumns.DESCRIPTION)));
        }

        return null;
    }

    @Override
    public List<Word> getAllWords()
    {
        List<Word> results = new ArrayList<Word>();
        Cursor query = db.query(DatabaseHelper.TABLE_WORDS, null, null, null, null, null, "language_id");

        while (query.moveToNext()) {
            Language language = findLanguage(query.getLong(query.getColumnIndex(DBColumns.LANGUAGE_ID)));
            results.add(Word.fromCursor(query, language));
        }
        query.close();
        return results;
    }

    @Override
    public Language findLanguage(long id) {
        Cursor query = getSelectQuery(DatabaseHelper.TABLE_LANGUAGES, id);

        if (query.moveToFirst())
        {
            Language language = Language.fromCursor(query);
            query.close();
            return language;
        }
        query.close();
        return null;
    }

    @Override
    public List<Language> getLanguages()
    {
        Cursor query = getSelectQuery(DatabaseHelper.TABLE_LANGUAGES);

        List<Language> languages = new ArrayList<Language>();
        while (query.moveToNext()) {

            languages.add(Language.fromCursor(query));
        }
        query.close();
        return languages;
    }

    @Override
    public List<Word> findWords(long languageId) {
        Cursor query = getSelectQuery(DatabaseHelper.TABLE_WORDS, DBColumns.LANGUAGE_ID, String.valueOf(languageId));

        List<Word> words = new ArrayList<Word>();
        while (query.moveToNext())
        {
            Language language = findLanguage(query.getLong(query.getColumnIndex(DBColumns.LANGUAGE_ID)));
            words.add(Word.fromCursor(query, language));
        }
        query.close();
        return words;
    }


}
