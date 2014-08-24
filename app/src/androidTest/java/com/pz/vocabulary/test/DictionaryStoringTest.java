package com.pz.vocabulary.test;

import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;

import java.util.List;

/**
 * Created by piotr on 07/07/14.
 */
public class DictionaryStoringTest extends VocabularyTest {
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


    public void testStoringTheSameMemoryTwoTimes()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        dbStore.insertWordsAndTranslation(polishKey, englishKey, memory);

        assertEquals(1, dbStore.getAllMemories().size());
    }

    public void testStoringTheSameTranslationTwoTimes()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        Word newPolishHome = new Word(polish, "dom");
        Word newEnglishHome = new Word(english, "house");
        dbStore.insertWordsAndTranslation(newPolishHome, newEnglishHome, memory);
        assertEquals(2, dbStore.getAllWords().size());
        List<Translation> meanings = dbStore.findMeanings(polishHome.getId());
        assertEquals(1, meanings.size());
    }

    public void testStoringTheSameTranslationWithMemoryOnSecond()
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

    public void testApostropheInWord()
    {
        Word apostrophe = english.newWord("mike's");

        dbStore.insertWordsAndTranslation(polishHome, apostrophe, null);

        assertTrue(dbStore.hasItems(Translation.class));

        List<Translation> translations = dbStore.findMeanings(apostrophe.getId());
        assertEquals(1, translations.size());

       dbStore.insertWordsAndTranslation(polishKey, apostrophe, null);

        assertEquals(3, dbStore.numberOfItems(Word.class));

        List<Word> w = dbStore.findWords(Language.ENGLISH);
        assertEquals("mike's", w.get(0).getSpelling());
    }

    public void testApostropheInMemory()
    {
        Word apostrophe = english.newWord("mike's");

        dbStore.insertWordsAndTranslation(polishHome, apostrophe, new Memory("piotr's"));

        assertTrue(dbStore.hasItems(Translation.class));

        List<Translation> translations = dbStore.findMeanings(apostrophe.getId());
        assertEquals(1, translations.size());

        assertEquals("piotr's", translations.get(0).getMemory().getDescription());
    }

}
