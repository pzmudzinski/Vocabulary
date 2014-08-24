package com.pz.vocabulary.test;

import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by piotr on 17/07/14.
 */
public class ToughWordTest extends VocabularyTest {

    private Quiz quiz ;
    private Question question;

    private float threshold = 0.8f;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);
        this.quiz = new Quiz(dbStore, Arrays.asList(polishHome, englishHome, polishKey, englishKey), false);
    }

    public void testNoToughWords()
    {
        correctAnswer();
        correctAnswer();
        correctAnswer();
        correctAnswer();
        quiz.store();

        List<Word> words = dbStore.getToughWords(new Date(0), threshold);
        assertEquals(0, words.size());
    }

    public void testAllToughWords()
    {
        wrongAnswer();
        wrongAnswer();
        wrongAnswer();
        wrongAnswer();
        quiz.store();

        List<Word> words = dbStore.getToughWords(new Date(0), threshold);
        assertEquals(4, words.size());
    }

    public void testHalfToughWords()
    {
        wrongAnswer();
        correctAnswer();
        wrongAnswer();
        correctAnswer();
        quiz.store();

        List<Word> toughWords = dbStore.getToughWords(new Date(0), threshold);

        assertEquals(2, toughWords.size());
    }

    public void testToughWordsSinceToday()
    {
        wrongAnswer();
        wrongAnswer();
        wrongAnswer();
        wrongAnswer();
        quiz.store();

        Translation translation = dbStore.findTranslation(polishKey.getId(), englishKey.getId());
        translation.setTimestamp(DateUtils.todayMinusXDays(5));
        updateTranslation(translation);

        List<Word> toughWords = dbStore.getToughWords(DateUtils.today(), threshold);
        assertEquals(2, toughWords.size());
    }

    public void testThreshold()
    {
        wrongAnswer();
        wrongAnswer();
        wrongAnswer();
        wrongAnswer();
        quiz.store();
        quiz = new Quiz(dbStore, Arrays.asList(polishHome, englishHome, polishKey, englishKey), false);

        correctAnswer();
        correctAnswer();
        correctAnswer();
        correctAnswer();
        quiz.store();

        List<Word> toughWords = dbStore.getToughWords(DateUtils.today(), 0.49f);
        assertEquals(0, toughWords.size());

        List<Word> toughWords2 = dbStore.getToughWords(DateUtils.today(), 0.50f);
        assertEquals(4, toughWords2.size());
    }

    private void correctAnswer()
    {
        //quiz.answer(answerFor(quiz.takeNextQuestion()));
    }

    private void wrongAnswer()
    {
        //quiz.takeNextQuestion();
        //quiz.skipQuestion();
    }
}
