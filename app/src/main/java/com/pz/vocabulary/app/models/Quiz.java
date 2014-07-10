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
import java.util.Stack;

/**
 * Created by piotr on 07/06/14.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_QUIZZES)
public class Quiz extends BaseEntity{
    private Dictionary dictionary;

    private Word currentWord;
    private List<Translation> currentMeanings;
    private List<Memory> currentTips;
    private Stack<Word> words = new Stack<Word>();
    private QuizResults results = new QuizResults();
    private int questionsNumber;
    private List<Long> insertedResponses = new LinkedList<Long>();

    @DatabaseField(columnName = DBColumns.RESULT)
    private float result;

    public Date getTsStart() {
        return tsStart;
    }

    public Date getTsEnd() {
        return tsEnd;
    }

    @DatabaseField(columnName = DBColumns.TIMESTAMP_START, dataType = DataType.DATE_LONG)
    private Date tsStart;

    @DatabaseField(columnName = DBColumns.TIMESTAMP_END, dataType = DataType.DATE_LONG)
    private Date tsEnd;

    public Quiz()
    {

    }

    public Quiz(Dictionary dictionary, List<Word> words) {
        this.dictionary = dictionary;
        Collections.shuffle(words);
        this.words = new Stack<Word>();
        this.words.addAll(words);
        this.results = new QuizResults(words.size());
        this.questionsNumber = words.size();
    }

    public boolean hasQuestionsLeft()
    {
        return words.size() > 0;
    }

    public int currentQuestionNumber()
    {
        return questionsNumber - words.size() ;
    }

    public int totalQuestionNumber()
    {
        return questionsNumber;
    }

    public Question takeNextQuestion()
    {
        if (currentWord == null && hasQuestionsLeft())
        {
            this.tsStart = new Date();
        }

        if (!hasQuestionsLeft()) {
            return null;
        }

        this.currentWord = dictionary.findWord(words.pop().getId());
        this.currentMeanings = dictionary.findMeanings(currentWord.getId());
        this.currentTips = new ArrayList<Memory>();
        for (Translation translation : currentMeanings)
        {
            if (translation.getMemory() != null)
                currentTips.add(translation.getMemory());
        }
        return createQuestion(currentWord);
    }

    public void skipQuestion()
    {
        results.addSkippedAnswer();
        insertedResponses.add(dictionary.insertResponse(currentWord.getId(), null, Dictionary.QuizQuestionResult.ResponseSkipped));
    }

    public boolean answer(String answer)
    {
        List<Translation> meanings = dictionary.findMeanings(currentWord.getId());

        if (!hasQuestionsLeft())
            this.tsEnd = new Date();

        for (Translation translation : meanings)
        {
            Word answerWord = new Word(translation.getTranslation().getLanguageID(), answer);
            if (answerWord.equals(translation.getTranslation()))
            {
                results.addCorrectAnswer();
                insertedResponses.add(dictionary.insertResponse(currentWord.getId(), answer, Dictionary.QuizQuestionResult.ResponseCorrect));
                return true;
            }
        }

        results.addWrongAnswer();
        insertedResponses.add(dictionary.insertResponse(currentWord.getId(), answer, Dictionary.QuizQuestionResult.ResponseWrong));
        return false;
    }

    public QuizResults getResults()
    {
        return results;
    }

    private Question createQuestion(Word word)
    {
        if (currentTips.size() == 0)
        {
            return new Question(word);
        }
        else
        {
            Random random = new Random();
            Memory memory = currentTips.get(random.nextInt(currentTips.size()));
            return new Question(word, memory);
        }
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
}
