package com.pz.vocabulary.test;

import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.QuizResults;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by piotr on 07/06/14.
 */
public class QuizTest extends VocabularyTest {

    private Quiz quiz;
    private List<Word> words = new ArrayList<Word>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        words.add(polishHome);
        words.add(englishHome);
        words.add(polishKey);
        words.add(englishKey);
        words.add(polishImportant);

    }

    public void testQuizCreation()
    {

        this.dbStore.insertWordsAndTranslation(polishHome, englishHome, null);
        this.dbStore.insertWordsAndTranslation(polishKey, englishKey, new Memory("wow!"));
        this.dbStore.insertWordsAndTranslation(polishImportant, englishKey, new Memory("oi"));

        this.quiz = new Quiz(dbStore, dbStore.getAllWords());
        assertTrue(quiz.hasQuestionsLeft());

        Question question = quiz.takeNextQuestion();
        assertTrue(words.contains(question.getWord()));

        // correct answer (normalized spelling)
        List<Translation> meanings = dbStore.findMeanings(question.getWord().getId());
        Translation translation = meanings.get(0);
        Word answer = dbStore.findWord(translation.getWordTo());

        boolean correctAnswer = quiz.answer(answer.getNormalizedSpelling());
        assertTrue(correctAnswer);

        Question question1 = quiz.takeNextQuestion();

        // wrong + skipped answer
        assertFalse(quiz.answer("nope"));
        quiz.skipQuestion();

        Question question2 = quiz.takeNextQuestion();

        meanings = dbStore.findMeanings(question2.getWord().getId());
        answer = dbStore.findWord(meanings.get(0).getWordTo());
        // correct answer (not normalized spelling)
        quiz.answer(answer.getSpelling());

        Question question3 = quiz.takeNextQuestion();

        // two wrongs + correct answer
        assertFalse(quiz.answer("!"));
        assertFalse(quiz.answer("?"));
        answer = dbStore.findWord(dbStore.findMeanings(question3.getWord().getId()).get(0).getWordTo());
        assertTrue(quiz.answer(answer.getSpelling()));

        // skip
        quiz.skipQuestion();
        quiz.takeNextQuestion();

        assertNull(quiz.takeNextQuestion());

        QuizResults results = quiz.getResults();

        assertEquals(words.size() , results.getQuestionsCount());

        assertEquals(2, results.getSkippedAnswers());

        assertEquals(3, results.getWrongAnswers());

        assertEquals(3, results.getCorrectAnswers());

    }

    public void testMultipleMeanings()
    {
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);
        dbStore.insertWordsAndTranslation(polishImportant, englishKey, null);

        List<Word> words1 = new ArrayList<Word>();
        words1.add(englishKey);
        this.quiz = new Quiz(dbStore, words1);
        Question question = quiz.takeNextQuestion();
        assertEquals(englishKey, question.getWord());
        boolean correct = quiz.answer(polishKey.getSpelling());
        assertTrue(correct);
    }

    public void testStoringResponse()
    {
        dbStore.insertWordsAndTranslation(polishKey, englishKey, null);
        this.quiz = new Quiz(dbStore, Arrays.asList(polishKey, englishKey));

        Question question = quiz.takeNextQuestion();

        quiz.answer("dupa");

        question = quiz.takeNextQuestion();

        Translation translation = dbStore.findMeanings(question.getWord().getId()).get(0);

        assertNotNull(translation.getTimestamp());
        String correctAnswer = translation.getTranslation().getSpelling();

        quiz.answer(correctAnswer);

        List<QuizResponse> allResponses = dbStore.getAllResponses();

        assertEquals(2, allResponses.size());

        List<QuizResponse> correctResponses = dbStore.findResponsesWithResult(Dictionary.QuizQuestionResult.ResponseCorrect);

        // check if timestamp was injected
        QuizResponse response = correctResponses.get(0);

        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().getTime() > 0);

        List<QuizResponse> wrongResponses = dbStore.findResponsesWithResult(Dictionary.QuizQuestionResult.ResponseWrong);

        assertEquals(1, correctResponses.size());
        assertEquals(1, wrongResponses.size());
    }
}
