package com.pz.vocabulary.app.models;

import android.widget.ListView;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.BaseEntity;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.QuizHistory;

import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by piotr on 07/06/14.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_QUIZZES)
public class Quiz extends BaseEntity{

    @DatabaseField(columnName = DBColumns.RESULT)
    private float result;

    @DatabaseField(columnName = DBColumns.TIMESTAMP_START, dataType = DataType.DATE_LONG)
    private Date tsStart;

    @DatabaseField(columnName = DBColumns.TIMESTAMP_END, dataType = DataType.DATE_LONG)
    private Date tsEnd;

    private List<Word> words = new ArrayList<Word>();
    private QuizResults results = new QuizResults();
    private int totalNumberOfQuestions;
    private List<Long> insertedResponses = new LinkedList<Long>();
    private Dictionary dictionary;

    private List<Question> questions;

    private Map<Integer, QuizResponse> responses = new HashMap<Integer, QuizResponse>();

    public Date getTsStart() {
        return tsStart;
    }

    public Date getTsEnd() {
        return tsEnd;
    }

    public Quiz()
    {

    }

    public Quiz(Dictionary dictionary, List<Word> words, boolean shuffle)
    {
        this.dictionary = dictionary;
        if (shuffle)
            Collections.shuffle(words);
        this.words = words;
        this.results = new QuizResults();
        this.totalNumberOfQuestions = words.size();
        this.questions = new ArrayList<Question>(words.size());
        for ( int i = 0 ; i < words.size() ; i++)
            questions.add(i, null);
        this.tsStart = new Date();
    }

    public Quiz(Dictionary dictionary, List<Word> words) {
        this(dictionary, words, true);
    }

    public int totalQuestionNumber()
    {
        return totalNumberOfQuestions;
    }

    public QuizResults getResults()
    {
        return results;
    }

    public void store()
    {
        this.result = results.getScore();

        if (this.tsEnd == null)
            this.tsEnd = new Date();

        long myID = this.dictionary.insertQuiz(this);
        this.dictionary.updateResponsesWithQuiz(insertedResponses, myID);
    }

    public float getScore()
    {
        return this.result;
    }

    public Question takeQuestion(int questionNumber) 
    {
        if (questionNumber > totalNumberOfQuestions) {
            return null;
        }

        if (questions.get(questionNumber) == null || questionNumber > questions.size())
        {
            Word word = dictionary.findWord(words.get(questionNumber).getId());
            List<Translation> meanings = dictionary.findMeanings(word.getId());
            List<Memory> tips = new ArrayList<Memory>();
            for (Translation translation : meanings)
            {
                if (translation.getMemory() != null)
                  tips.add(translation.getMemory());
            }

            if (tips.size() == 0)
                questions.add(questionNumber, new Question(word));
            else {
                Random random = new Random();
                Memory memory = tips.get(random.nextInt(tips.size()));
                questions.add(questionNumber, new Question(word, memory));
            }
        }

        return questions.get(questionNumber);
    }

    private long getIdForWord(int questionsNumber)
    {
        return words.get(questionsNumber).getId();
    }

    public void skipQuestion(int questionNumber) {
        results.addSkippedAnswer();
        QuizResponse response = dictionary.insertResponse(getIdForWord(questionNumber), null, Dictionary.QuizQuestionResult.ResponseSkipped);
        insertedResponses.add(response.getId());
        responses.put(questionNumber, response);
    }

    public QuizResponse getResponseFor(int questionNumber)
    {
        return responses.get(questionNumber);
    }

    public boolean answer(int questionNumber, String answer)
    {
        long id = getIdForWord(questionNumber);
        List<Translation> meanings = dictionary.findMeanings(id);

        QuizResponse response = null;
        boolean isCorrect = false;
        for (Translation translation : meanings)
        {
            Word answerWord = new Word(translation.getTranslation().getLanguageID(), answer);
            if ( answerWord.equals(translation.getTranslation()))
            {
                results.addCorrectAnswer();
                response = dictionary.insertResponse(id, answer, Dictionary.QuizQuestionResult.ResponseCorrect);
                isCorrect = true;
            }
        }

        if (!isCorrect)
        {
            results.addWrongAnswer();
            response = dictionary.insertResponse(id, answer, Dictionary.QuizQuestionResult.ResponseWrong);
        }

        insertedResponses.add(response.getId());
        responses.put(questionNumber, response);
        return isCorrect;
    }

    public QuizResponse acceptAnswer(int questionNumber)
    {
        QuizResponse currentResponse = responses.get(questionNumber);
        if (currentResponse == null)
            return null; // wtf

        insertedResponses.remove(currentResponse.getId());
        responses.remove(questionNumber);
        dictionary.deleteResponse(currentResponse.getId());

        if (currentResponse.getResult() == QuizHistory.QuizQuestionResult.ResponseSkipped)
            results.removeSkippedAnswer();
        else if (currentResponse.getResult() == QuizHistory.QuizQuestionResult.ResponseWrong)
            results.removeWrongAnswer();

        QuizResponse newResponse = dictionary.insertResponse(currentResponse.getWordFrom().getId(), currentResponse.getResponse(), QuizHistory.QuizQuestionResult.ResponseCorrect);
        insertedResponses.add(newResponse.getId());
        responses.put(questionNumber, newResponse);

        results.addCorrectAnswer();

        return newResponse;
    }

    public QuizResponse acceptAnswerAndAddMeaning(int questionNumber)
    {
        String usersAnswer = responses.get(questionNumber).getResponse();
        QuizResponse correctResponse = acceptAnswer(questionNumber);
        Word wordFrom = dictionary.findWord(correctResponse.getWordFrom().getId());

        List<Language> languages = dictionary.getLanguages();
        Language otherLanguage = null;
        for ( Language language : languages)
        {
            if (language.getId() != wordFrom.getLanguageID()) {
                otherLanguage = language;
                break;
            }
        }

        if (otherLanguage == null)
            return null;

        Word wordTo = otherLanguage.newWord(usersAnswer);

        dictionary.insertWordsAndTranslation(wordFrom, wordTo, null);
        return correctResponse;
    }
}
