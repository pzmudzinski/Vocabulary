package com.pz.vocabulary.test;

import android.test.AndroidTestCase;

import com.pz.vocabulary.app.models.Language;
import com.pz.vocabulary.app.models.Memory;
import com.pz.vocabulary.app.models.Translation;
import com.pz.vocabulary.app.models.Word;
import com.pz.vocabulary.app.sql.DatabaseHelper;
import com.pz.vocabulary.app.sql.DatabaseStore;
import com.pz.vocabulary.app.utils.Logger;

import java.util.List;

/**
 * Created by piotr on 04/06/14.
 */
public class DatabaseStoreTest extends AndroidTestCase {

    private DatabaseStore dbStore;
    private DatabaseHelper dbHelper;

    private Word polishHome = new Word(Language.POLISH, "dom");
    private Word englishHome = new Word(Language.ENGLISH, "house");
    private Word polishKey = new Word(Language.POLISH, "klucz");
    private Word polishImportant = new Word(Language.POLISH, "ważny");
    private Word englishKey = new Word(Language.ENGLISH, "key");
    private Memory memory = new Memory("hi-hi-hi");
    private Memory memory2 = new Memory("hi-hi-hi2");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.dbHelper = new DatabaseHelper(getContext(), null);
        this.dbStore = new DatabaseStore(getContext(), dbHelper);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbStore.close();
    }

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
        Language polish = dbStore.getLanguage(Language.POLISH);
        Language english = dbStore.getLanguage(Language.ENGLISH);
        dbStore.insertWordsAndTranslation(polish.newWord("dom"), english.newWord("house"), null);
        dbStore.insertWordsAndTranslation(polish.newWord("kot"), english.newWord("cat"), null);

        assertEquals(4, dbStore.getAllWords().size());
    }

    public void testStoringTheSameWordsTwice()
    {
        assertEquals(-1, dbStore.findWord(polishHome.getLanguage(), polishHome.getNormalizedSpelling()));
        long id1 = dbStore.insertWord(polishHome);
        assertNotNull(dbStore.findWord(polishHome.getId()));
        long id2 = dbStore.insertWord(polishHome);
        assertEquals(id1, id2);
    }

    public void testFindingMethods()
    {
        dbStore.insertWord(polishHome);

        assertEquals(polishHome.getId(), dbStore.findWord(polishHome.getLanguage(), polishHome.getNormalizedSpelling()));

        assertEquals(-1, dbStore.findWord(englishHome.getLanguage(), englishHome.getNormalizedSpelling()));
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

        long id = dbStore.findWord(polishImportant.getLanguage(), polishImportant.getNormalizedSpelling());
        Word word = dbStore.findWord(id);
        assertNotNull(word);
    }

    public void testFindingWithoutSpecialCharacters()
    {
        dbStore.insertWord(polishImportant);

        long id = dbStore.findWord(polishImportant.getLanguage(), "wazny");

        Word word = dbStore.findWord(id);

        List<Word> words = dbStore.getAllWords();
        Logger.log("db", words.toString());

        assertNotNull(word);
    }

    public void testFindingCaseSensitive()
    {
        dbStore.insertWord(polishImportant);

        long id = dbStore.findWord(polishImportant.getLanguage(), "wAzNY");

        assertNotNull(dbStore.findWord(id));
    }

    public void testFindingLanguage()
    {
        Language polish = dbStore.getLanguage(Language.POLISH);
        Language english = dbStore.getLanguage(Language.ENGLISH);

        assertNotNull(polish);
        assertNotNull(english);
    }
}
