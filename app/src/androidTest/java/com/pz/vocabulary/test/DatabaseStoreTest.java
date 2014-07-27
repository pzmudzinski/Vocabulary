package com.pz.vocabulary.test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.utils.DateUtils;
import com.pz.vocabulary.app.utils.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by piotr on 04/06/14.
 */
public class DatabaseStoreTest extends VocabularyTest {

    public void testInitialLanguages()
    {
        Logger.log("db", dbStore.getLanguages().toString());
        assertEquals(2, dbStore.getLanguages().size());
    }

    public void testFindingMethods()
    {
        dbStore.insertWord(polishHome);

        assertEquals(polishHome.getId(), dbStore.findWord(polishHome.getLanguageID(), polishHome.getNormalizedSpelling()));

        assertEquals(-1, dbStore.findWord(englishHome.getLanguageID(), englishHome.getNormalizedSpelling()));
    }

    public void testFindingBySpelling()
    {
        dbStore.insertWord(polishImportant);

        long id = dbStore.findWord(polishImportant.getLanguageID(), polishImportant.getNormalizedSpelling());
        Word word = dbStore.findWord(id);
        assertNotNull(word);
    }

    public void testFindingWithoutSpecialCharacters()
    {
        dbStore.insertWord(polishImportant);

        long id = dbStore.findWord(polishImportant.getLanguageID(), "wazny");

        Word word = dbStore.findWord(id);

        List<Word> words = dbStore.getAllWords();
        Logger.log("db", words.toString());

        assertNotNull(word);
    }

    public void testFindingCaseSensitive()
    {
        dbStore.insertWord(polishImportant);

        long id = dbStore.findWord(polishImportant.getLanguageID(), "wAzNY");

        assertNotNull(dbStore.findWord(id));
    }

    public void testFindingLanguage()
    {
        Language polish = dbStore.findLanguage(Language.POLISH);
        Language english = dbStore.findLanguage(Language.ENGLISH);

        assertNotNull(polish);
        assertNotNull(english);
    }

    public void testGettingAllLanguages()
    {
        List<Language> langs = dbStore.getLanguages();

        assertTrue(langs.size() > 0);
    }

    public void testFindingWordsByLanguage()
    {
        dbStore.insertWord(polishHome);
        dbStore.insertWord(englishHome);
        dbStore.insertWord(polishImportant);

        List<Word> polish = dbStore.findWords(Language.POLISH);
        List<Word> english = dbStore.findWords(Language.ENGLISH);

        assertEquals(2, polish.size());
        assertEquals(1, english.size());
     }

    public void testGettingFullDictionary()
    {
        dbStore.insertWord(polishHome);
        dbStore.insertWord(englishKey);
        dbStore.insertWordsAndTranslation(polishHome, englishKey, null);

        Map<Language, List<Word>> dict = dbStore.getWordsByLanguage();
        assertEquals(2,dict.keySet().size());
        List<Word> polish = dict.get(dbStore.findLanguage(Language.POLISH));
        List<Word> english = dict.get(dbStore.findLanguage(Language.ENGLISH));

        assertEquals(1, polish.size());
        assertEquals(1, english.size());
    }

    public void testGettingWordsUntilNow()
    {
        dbStore.insertWord(polishHome);
        dbStore.insertWord(englishHome);
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);

        List<Word> wordsSinceToday = dbStore.getWordsInsertedSince(DateUtils.today());
        assertEquals(2, wordsSinceToday.size());

        dbStore.insertWordsAndTranslation(polishImportant, englishKey, null);

        wordsSinceToday = dbStore.getWordsInsertedSince(DateUtils.today());
        assertEquals(4, wordsSinceToday.size());

        List<Word> wordsSinceNow = dbStore.getWordsInsertedSince(new Date());
        assertEquals(0, wordsSinceNow.size());

        List<Word> wordsSinceYesterday = dbStore.getWordsInsertedSince(DateUtils.todayMinusXDays(1));
        assertEquals(4, wordsSinceYesterday.size());

        List<Word> wordsSinceWeek = dbStore.getWordsInsertedSince(DateUtils.startWeek());
        assertEquals(4, wordsSinceWeek.size());

        List<Word> wordsSinceMonth = dbStore.getWordsInsertedSince(DateUtils.startMonth());
        assertEquals(4, wordsSinceMonth.size());
    }

    public void testGettingWordsOutOfDate()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        dbStore.insertWordsAndTranslation(polishImportant, englishKey, null);

        try {
            Dao<Translation, Long> translations = dbHelper.getDao(Translation.class);
            Translation translation = translations.queryForAll().get(0);
            translation.setTimestamp(DateUtils.todayMinusXDays(7));

            //translations.update(translation);
            // don't use udpate method
            // it's trying to match translation based on both id and timestamp
            // updatebuilder is more 'low-lewel'
            updateTranslation(translation);
            List<Word> words = dbStore.getWordsInsertedSince(DateUtils.today());
            assertEquals(2, words.size());

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }

    }

    public void testFindingTranslation()
    {
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);
        Translation translation = dbStore.findTranslation(polishKey.getId(), englishKey.getId());
        assertNotNull(translation);
    }

    public void testGettingWordSinceWhenItHasTwoMeanings()
    {
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);
        dbStore.insertWordsAndTranslation(polishImportant, englishKey, null);

        Translation translation = dbStore.findTranslation(polishKey.getId(), englishKey.getId());
        translation.setTimestamp(DateUtils.todayMinusXDays(5));
        updateTranslation(translation);

        List<Word> wordsSinceYesterday = dbStore.getWordsInsertedSince(DateUtils.todayMinusXDays(1));
        assertEquals(2, wordsSinceYesterday.size());
        List<Long> ids = new ArrayList<Long>();
        ids.add(wordsSinceYesterday.get(0).getId());
        ids.add(wordsSinceYesterday.get(1).getId());
        assertTrue(ids.contains(englishKey.getId()));
        assertTrue(ids.contains(polishImportant.getId()));
    }

    public void testHasItems()
    {
        assertFalse(dbStore.hasItems(Word.class));
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        assertTrue(dbStore.hasItems(Word.class));
        assertFalse(dbStore.hasItems(Memory.class));

        Translation translation = dbStore.findTranslation(polishHome.getId(), englishHome.getId());
        dbStore.insertMemory(memory);
        dbStore.addMemoryToTranslation(memory.getId(), translation.getId());

        assertTrue(dbStore.hasItems(Memory.class));
        assertTrue(dbStore.hasItems(Translation.class));

    }


}
