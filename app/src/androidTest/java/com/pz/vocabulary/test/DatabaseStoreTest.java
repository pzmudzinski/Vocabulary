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

    public void testStoringWords()
    {
        long id1 = dbStore.insertWord(polishHome);
        long id2 = dbStore.insertWord(englishHome);

        List<Word> words = dbStore.getAllWords();
        assertEquals(id1, words.get(0).getId());
        assertEquals(id2, words.get(1).getId());
        assertEquals(2, words.size());
    }

    public void testStoringManyWords()
    {
        Language polish = dbStore.findLanguage(Language.POLISH);
        Language english = dbStore.findLanguage(Language.ENGLISH);
        dbStore.insertWordsAndTranslation(polish.newWord("dom"), english.newWord("house"), null);
        dbStore.insertWordsAndTranslation(polish.newWord("kot"), english.newWord("cat"), null);

        assertEquals(4, dbStore.getAllWords().size());
    }

    public void testStoringTheSameWordsTwice()
    {
        assertEquals(-1, dbStore.findWord(polishHome.getLanguageID(), polishHome.getNormalizedSpelling()));
        long id1 = dbStore.insertWord(polishHome);
        assertNotNull(dbStore.findWord(polishHome.getId()));
        long id2 = dbStore.insertWord(polishHome);
        assertEquals(id1, id2);
    }

    public void testFindingMethods()
    {
        dbStore.insertWord(polishHome);

        assertEquals(polishHome.getId(), dbStore.findWord(polishHome.getLanguageID(), polishHome.getNormalizedSpelling()));

        assertEquals(-1, dbStore.findWord(englishHome.getLanguageID(), englishHome.getNormalizedSpelling()));
    }

    public void testSimpleTranslation()
    {
        long id1 = dbStore.insertWord(polishHome);
        long id2 = dbStore.insertWord(englishHome);

        dbStore.insertTranslation(id1, id2);

        List<Translation> meanings = dbStore.findMeanings(polishHome.getId());

        assertEquals(id2, meanings.get(0).getWordTo());
    }

    public void testMultipleTranslations()
    {
        long id1 = dbStore.insertWord(polishKey);
        long id2 = dbStore.insertWord(englishKey);
        long id3 = dbStore.insertWord(polishImportant);

        dbStore.insertTranslation(polishKey.getId(), englishKey.getId() );
        dbStore.insertTranslation(polishImportant.getId(), englishKey.getId());

        List<Translation> meanings = dbStore.findMeanings(englishKey.getId());

        assertEquals(2, meanings.size());
        assertEquals(id1, meanings.get(0).getWordTo());
        assertEquals(id3, meanings.get(1).getWordTo());
    }

    public void testAddingTranslationWithMemory()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        Translation translation = dbStore.findMeanings(polishHome.getId()).get(0);

        assertEquals("hi-hi-hi", translation.getMemory().getDescription());
    }

    public void testAddingMultipleMemories()
    {
        dbStore.insertWordsAndTranslation(polishKey, englishKey, memory);
        dbStore.insertWordsAndTranslation(polishImportant, englishKey, memory2);

        List<Translation> translations = dbStore.findMeanings(englishKey.getId());
        Memory[] memories = new Memory[] { translations.get(0).getMemory(), translations.get(1).getMemory()};

        assertEquals(2, memories.length);
        assertEquals("hi-hi-hi", memories[0].getDescription());
        assertEquals("hi-hi-hi2", memories[1].getDescription());
    }

    public void testAddingNullMemory()
    {
        try {
            dbStore.insertWordsAndTranslation(polishKey, englishKey, null);
        } catch (Exception e)
        {
            fail();
        }

    }

    public void testAddingMemoryTwice()
    {
        Memory memory1 = new Memory("haha!");
        dbStore.insertMemory(memory1);

        Memory insertedMemory = dbStore.findMemory(memory1.getId());
        insertedMemory.setDescription("oh no");

        dbStore.insertMemory(insertedMemory);

        assertEquals("oh no", dbStore.findMemory(insertedMemory.getId()).getDescription());
    }

    public void testAddingMemoryToExistingTranslation()
    {
        dbStore.insertWord(polishHome);
        dbStore.insertWord(englishHome);

        dbStore.insertTranslation(polishHome.getId(), englishHome.getId());

        Translation translation = dbStore.findMeanings(englishHome.getId()).get(0);

        assertNull(translation.getMemory());

        Memory memory1 = new Memory("alala");
        dbStore.insertMemory(memory1);

        dbStore.addMemoryToTranslation(memory1.getId(), translation.getId());

        translation = dbStore.findMeanings(englishHome.getId()).get(0);
        assertEquals(memory1.getId(), translation.getMemory().getId());
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

    public void testInsertTheSameMemoryTwoTimes()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        dbStore.insertWordsAndTranslation(polishKey, englishKey, memory);

        assertEquals(1, dbStore.getAllMemories().size());
    }

    public void testInsertTheSameTranslationTwoTimes()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        Word newPolishHome = new Word(polish, "dom");
        Word newEnglishHome = new Word(english, "house");
        dbStore.insertWordsAndTranslation(newPolishHome, newEnglishHome, memory);
        assertEquals(2, dbStore.getAllWords().size());
        List<Translation> meanings = dbStore.findMeanings(polishHome.getId());
        assertEquals(1, meanings.size());
    }

    public void testInsertTheSameTranslationWithMemoryOnSecond()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        Word newPolishHome = new Word(polish, "dom");
        Word newEnglishHome = new Word(english, "house");
        dbStore.insertWordsAndTranslation(newPolishHome, newEnglishHome, memory);
        assertEquals(2, dbStore.getAllWords().size());
        List<Translation> meanings = dbStore.findMeanings(newPolishHome.getId());
        assertEquals(1, meanings.size());
        Translation translation = meanings.get(0);
        assertEquals(translation.getMemory().getDescription(), memory.getDescription());
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
        assertTrue(wordsSinceToday.contains(polishHome));
        assertTrue(wordsSinceToday.contains(englishHome));

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
        assertTrue(wordsSinceYesterday.contains(englishKey));
        assertTrue(wordsSinceYesterday.contains(polishImportant));
    }

    private void updateTranslation(Translation translation)
    {
        Dao<Translation, Long> translations = null;
        try {
            translations = dbHelper.getDao(Translation.class);
            UpdateBuilder<Translation, Long> updateBuilder = translations.updateBuilder();
            updateBuilder.updateColumnValue(DBColumns.TIMESTAMP, translation.getTimestamp());
            updateBuilder.where().eq(DBColumns.ID, translation.getId());

            String query = updateBuilder.prepareStatementString();
            translations.update(updateBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
