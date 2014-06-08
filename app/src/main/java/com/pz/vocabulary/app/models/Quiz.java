package com.pz.vocabulary.app.models;

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

    public Quiz(Dictionary dictionary, List<Word> words) {
        this.dictionary = dictionary;
        Collections.shuffle(words);
        this.words = new Stack<Word>();
        this.words.addAll(words);
        this.results = new QuizResults(words.size());
    }

    public boolean hasQuestionsLeft()
    {
        return words.size() > 0;
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
    }

    public boolean answer(String answer)
    {
        List<Translation> meanings = dictionary.findMeanings(currentWord.getId());
        List<Word> toWords = new ArrayList<Word>();

        for (Translation translation : meanings)
        {
            toWords.add(dictionary.findWord(translation.getWordTo()));
        }

        for (Word word : toWords)
        {
            Word answerWord = new Word(word.getLanguage(), answer);
            if (answerWord.equals(word))
            {
                results.addCorrectAnswer();
                return true;
            }
        }

        results.addWrongAnswer();
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
