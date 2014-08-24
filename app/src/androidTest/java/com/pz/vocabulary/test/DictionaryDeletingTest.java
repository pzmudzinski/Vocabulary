package com.pz.vocabulary.test;

import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by piotr on 07/07/14.
 */
public class DictionaryDeletingTest extends VocabularyTest {

    public void testDeletingOneToOneWord()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        boolean deleteIsSuccess =  dbStore.deleteWord(polishHome.getId());
        assertTrue(deleteIsSuccess);

        Word englishHomeNow = dbStore.findWord(englishHome.getId());
        assertNull(englishHomeNow);
        assertFalse(dbStore.hasItems(Word.class));
    }

    public void testDeletingWordWhenManyRecords()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);
        dbStore.deleteWord(polishHome.getId());
        assertNull(dbStore.findWord(englishHome.getId()));
    }

    public void testWordToDeleting()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        dbStore.deleteWord(englishHome.getId());
        assertFalse(dbStore.hasItems(Word.class));
    }

    public void testWordToDeletingWithMemory()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        dbStore.deleteWord(englishHome.getId());
        assertFalse(dbStore.hasItems(Word.class));
        assertFalse(dbStore.hasItems(Memory.class));
    }

    public void testDeletingWordWithMemory()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        assertTrue(dbStore.deleteWord(polishHome.getId()));
        Memory memoryNow = dbStore.findMemory(memory.getId());
        assertNull(memoryNow);
    }

    public void testDeletingOneToMoreWord()
    {
        dbStore.insertWordsAndTranslation(polishImportant, englishKey, null);
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);

        assertTrue(dbStore.deleteWord(englishKey.getId()));
        assertFalse(dbStore.hasItems(Word.class));
    }

    public void testDeletingWordWithTranslationUsedSomewhereElse()
    {
        dbStore.insertWordsAndTranslation(polishImportant, englishKey, null);
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);

        assertTrue(dbStore.deleteWord(polishImportant.getId()));
        assertNull(dbStore.findWord(polishImportant.getId()));
        List<Word> words = dbStore.getAllWords();
        assertEquals(2, words.size());

        List<Translation> meanings = dbStore.findMeanings(polishKey.getId());
        assertEquals(1, meanings.size());

        assertEquals(englishKey, meanings.get(0).getTranslation());
    }

    public void testDeletingWordWithMemoryUsedSomewhereElse()
    {
        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
        dbStore.insertWordsAndTranslation(polishImportant, englishKey, memory);
        assertTrue(dbStore.deleteWord(polishHome.getId()));
        assertNull(dbStore.findWord(englishHome.getId()));
        assertNotNull(dbStore.findMemory(memory.getId()));

        List<Translation> meanings = dbStore.findMeanings(polishImportant.getId());
        assertEquals(meanings.get(0).getMemory().getId(), memory.getId());
    }

//    public void testDeletingWordWithQuizHistory()
//    {
//        dbStore.insertWordsAndTranslation(polishHome, englishHome, memory);
//
//        Quiz quiz = new Quiz(dbStore, Arrays.asList(polishHome, englishHome), false);
//        quiz.takeNextQuestion();
//        quiz.skipQuestion();
//        quiz.takeNextQuestion();
//        quiz.skipQuestion();
//        quiz.store();
//
//        dbStore.deleteWord(polishHome.getId());
//
//        assertFalse(dbStore.hasItems(QuizResponse.class));
//    }
//
//    public void testDeletingWordWithQuizHistoryAndTwoMeanings() throws SQLException {
//        dbStore.insertWordsAndTranslation(polishKey, englishKey, memory);
//        dbStore.insertWordsAndTranslation(polishImportant, englishKey, memory);
//
//        Quiz quiz = new Quiz(dbStore, Arrays.asList(polishKey, englishKey, polishImportant, englishKey), false);
//        quiz.takeNextQuestion();
//        quiz.skipQuestion();
//        quiz.takeNextQuestion();
//        quiz.skipQuestion();
//        quiz.takeNextQuestion();
//        quiz.skipQuestion();
//        quiz.takeNextQuestion();
//        quiz.skipQuestion();
//        quiz.store();
//
//        dbStore.deleteWord(polishKey.getId());
//
//        assertEquals(3, dbStore.numberOfItems(QuizResponse.class));
//        List<QuizResponse> responses = dbHelper.getDao(QuizResponse.class).queryForAll();
//
//        for (QuizResponse response : responses)
//        {
//            assertTrue(response.getWordFrom().getId() == polishImportant.getId() || response.getWordFrom().getId() == englishKey.getId());
//        }
//    }

    public void testDeletingNotExistingWord()
    {
        assertFalse(dbStore.deleteWord(255));
    }
}
