package com.pz.vocabulary.app.models;

import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by piotr on 07/06/14.
 */
public class Quiz {
    private Dictionary dictionary;

    private Word currentWord;
    private Language currentLanguage;
    private List<Translation> currentMeanings;
    private List<Memory> currentTips;
    private Stack<Word> words = new Stack<Word>();
    private QuizResults results = new QuizResults();
    private int questionsNumber;

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
        if (!hasQuestionsLeft())
            return null;

        this.currentWord = words.pop();
        this.currentLanguage = dictionary.findLanguage(currentWord.getLanguageID());
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
        dictionary.insertResponse(currentWord.getId(), null, Dictionary.QuizQuestionResult.ResponseSkipped);
    }

    public boolean answer(String answer)
    {
        List<Translation> meanings = dictionary.findMeanings(currentWord.getId());

        for (Translation translation : meanings)
        {
            Word answerWord = new Word(translation.getTranslation().getLanguage(), answer);
            if (answerWord.equals(translation.getTranslation()))
            {
                results.addCorrectAnswer();
                dictionary.insertResponse(currentWord.getId(), answer, Dictionary.QuizQuestionResult.ResponseCorrect);
                return true;
            }
        }

        results.addWrongAnswer();
        dictionary.insertResponse(currentWord.getId(), answer, Dictionary.QuizQuestionResult.ResponseWrong);
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
}
