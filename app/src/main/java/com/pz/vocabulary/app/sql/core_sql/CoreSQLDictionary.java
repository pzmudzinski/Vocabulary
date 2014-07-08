package com.pz.vocabulary.app.sql.core_sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.SQLStore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piotr on 14/05/14.
 */
@Deprecated()
public class CoreSQLDictionary extends SQLStore implements Dictionary {

    public CoreSQLDictionary(Context context, SQLiteOpenHelper helper) {
        super(context, helper);
    }

    @Override
    public boolean hasItems(Class clz) {
        return false;
    }

    @Override
    public long numberOfItems(Class clz) {
        return 0;
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
            id = db.insert(CoreSQLDatabaseHelper.TABLE_WORDS, null, values);
            word.setId(id);
        } else
        {
            db.update(CoreSQLDatabaseHelper.TABLE_WORDS, values, DBColumns.ID + "=?", new String[]{String.valueOf(id)});
        }

        return id;
    }

    @Override
    public long findWord(long langID, String spelling)
    {
        Cursor cursor = db.query(CoreSQLDatabaseHelper.TABLE_WORDS, new String[]{DBColumns.ID}, DBColumns.NORMALIZED_SPELLING + "='" + spelling + "' AND " + DBColumns.LANGUAGE_ID + "=" + langID, null, null, null, null);
        if (cursor.moveToFirst())
            return cursor.getLong(0);

        return -1;
    }

    @Override
    public Word findWord(long id)
    {
        Cursor cursor = getSelectQuery(CoreSQLDatabaseHelper.TABLE_WORDS, id);
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
            db.update(CoreSQLDatabaseHelper.TABLE_MEMORIES, values, DBColumns.ID + " = ?", new String[]{String.valueOf(memory.getId())});
        } else {
            id = db.insert(CoreSQLDatabaseHelper.TABLE_MEMORIES, null, values);
            memory.setId(id);
        }

        return memory.getId();
    }

    @Override
    public void addMemoryToTranslation(long memoryId, long translationId)
    {
        ContentValues values = new ContentValues();
        values.put(DBColumns.MEMORY_ID, memoryId);
        db.update(CoreSQLDatabaseHelper.TABLE_TRANSLATIONS, values,DBColumns.ID +" = ?", new String[] {String.valueOf(translationId)});
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

        long id = db.insert(CoreSQLDatabaseHelper.TABLE_TRANSLATIONS, null, values);
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
    public Translation findTranslation(long wordFrom, long wordTo) {
        return null;
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

            translation = new Translation(translationId, findWord(wordToId), findMemory(memoryId));
            results.add(translation);
        }
        return results;
    }

    @Override
    public Memory findMemory(long memoryId)
    {
        Cursor cursor = getSelectQuery(CoreSQLDatabaseHelper.TABLE_MEMORIES, memoryId);

        while (cursor.moveToFirst())
        {
            return new Memory(memoryId, cursor.getString(cursor.getColumnIndex(DBColumns.DESCRIPTION)));
        }

        return null;
    }

    public Memory findMemory(String description)
    {
        Cursor cursor = getSelectQuery(CoreSQLDatabaseHelper.TABLE_MEMORIES, DBColumns.DESCRIPTION, description);

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
        Cursor query = db.query(CoreSQLDatabaseHelper.TABLE_WORDS, null, null, null, null, null, "language_id");

        while (query.moveToNext()) {
            Language language = findLanguage(query.getLong(query.getColumnIndex(DBColumns.LANGUAGE_ID)));
            results.add(Word.fromCursor(query, language));
        }
        query.close();
        return results;
    }

    @Override
    public Language findLanguage(long id) {
        Cursor query = getSelectQuery(CoreSQLDatabaseHelper.TABLE_LANGUAGES, id);

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
        Cursor query = getSelectQuery(CoreSQLDatabaseHelper.TABLE_LANGUAGES);

        List<Language> languages = new ArrayList<Language>();
        while (query.moveToNext()) {

            languages.add(Language.fromCursor(query));
        }
        query.close();
        return languages;
    }

    @Override
    public List<Memory> getAllMemories() {
        return null;
    }

    @Override
    public void destroyEverything() {

    }

    @Override
    public List<Word> findWords(long languageId) {
        Cursor query = getSelectQuery(CoreSQLDatabaseHelper.TABLE_WORDS, DBColumns.LANGUAGE_ID, String.valueOf(languageId));

        List<Word> words = new ArrayList<Word>();
        while (query.moveToNext())
        {
            Language language = findLanguage(query.getLong(query.getColumnIndex(DBColumns.LANGUAGE_ID)));
            words.add(Word.fromCursor(query, language));
        }
        query.close();
        return words;
    }

    @Override
    public Map<Language, List<Word>> getWordsByLanguage() {
        List<Language> languages = getLanguages();
        Map<Language, List<Word>> dict = new HashMap<Language, List<Word>>();
        for (Language language : languages)
        {
            List<Word> words = findWords(language.getId());
            dict.put(language, words);
        }
        return dict;
    }

    @Override
    public List<Word> getWordsInsertedSince(Date date) {
        return null;
    }

    @Override
    public long insertResponse(long wordFrom, String response, QuizQuestionResult result) {
        ContentValues values = new ContentValues();
        values.put(DBColumns.WORD_FROM, wordFrom);
        values.put(DBColumns.RESPONSE, response);
        values.put(DBColumns.RESULT, result.ordinal());

        return db.insert(CoreSQLDatabaseHelper.TABLE_RESPONSES, null, values);
    }

    @Override
    public List<QuizResponse> findResponsesWithResult(QuizQuestionResult result) {
        Cursor query = getSelectQuery(CoreSQLDatabaseHelper.TABLE_RESPONSES, DBColumns.RESULT, String.valueOf(result.ordinal()));
        List<QuizResponse> responses = new ArrayList<QuizResponse>();
        while (query.moveToNext())
        {
            responses.add(QuizResponse.fromQuery(query, this));
        }

        query.close();
        return responses;
    }

    @Override
    public List<QuizResponse> getAllResponses() {
        return null;
    }

    @Override
    public long insertQuiz(Quiz quiz) {
        return 0;
    }

    @Override
    public void updateResponsesWithQuiz(List<Long> responsesID, long quizID) {

    }

    @Override
    public long numberOfAllResponses() {
        return 0;
    }

    @Override
    public long numberOfResponsesWithResult(QuizQuestionResult result) {
        return 0;
    }

    @Override
    public float quizAverageScore() {
        return 0;
    }

    @Override
    public org.joda.time.Period quizTotalTimeSpent() {
        return null;
    }

    @Override
    public org.joda.time.Period quizAverageTimeSpent() {
        return null;
    }


}
