package com.pz.vocabulary.test;

import android.test.AndroidTestCase;

import com.pz.vocabulary.app.models.Language;
import com.pz.vocabulary.app.models.Memory;
import com.pz.vocabulary.app.models.Translation;
import com.pz.vocabulary.app.models.Word;
import com.pz.vocabulary.app.sql.DatabaseHelper;
import com.pz.vocabulary.app.sql.DatabaseStore;

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

    public void testStoringWords()
    {
        long id1 = dbStore.insertWord(polishHome);
        long id2 = dbStore.insertWord(englishHome);

        List<Word> words = dbStore.getAllWords();
        assertEquals(id1, words.get(0).getId());
        assertEquals(id2, words.get(1).getId());
        assertEquals(2, words.size());
    }

    public void testStoringTheSameWordsTwice()
    {
        assertEquals(-1, dbStore.findWord(polishHome.getSpelling()));
        long id1 = dbStore.insertWord(polishHome);
        assertNotNull(dbStore.findWord(polishHome.getId()));
        long id2 = dbStore.insertWord(polishHome);
        assertEquals(id1, id2);
    }

    public void testFindingMethods()
    {
        dbStore.insertWord(polishHome);

        assertEquals(polishHome.getId(), dbStore.findWord(polishHome.getSpelling()));

        assertEquals(-1, dbStore.findWord(englishHome.getSpelling()));
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
        dbStore.insertWordAndTranslation(polishHome, englishHome, memory);
        Translation translation = dbStore.findMeanings(polishHome.getId()).get(0);

        assertEquals("hi-hi-hi", translation.getMemory().getDescription());
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
}
