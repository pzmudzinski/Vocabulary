package com.pz.vocabulary.app.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.models.db.BaseEntity;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
        insertedResponses.add(dictionary.insertResponse(getIdForWord(questionNumber), null, Dictionary.QuizQuestionResult.ResponseSkipped));
    }

    public boolean answer(int questionNumber, String answer)
    {
        long id = getIdForWord(questionNumber);
        List<Translation> meanings = dictionary.findMeanings(id);

        for (Translation translation : meanings)
        {
            Word answerWord = new Word(translation.getTranslation().getLanguageID(), answer);
            if ( answerWord.equals(translation.getTranslation()))
            {
                results.addCorrectAnswer();
                insertedResponses.add(dictionary.insertResponse(id, answer, Dictionary.QuizQuestionResult.ResponseCorrect));
                return true;
            }
        }

        results.addWrongAnswer();
        insertedResponses.add(dictionary.insertResponse(id, answer, Dictionary.QuizQuestionResult.ResponseWrong));
        return false;
    }
}
