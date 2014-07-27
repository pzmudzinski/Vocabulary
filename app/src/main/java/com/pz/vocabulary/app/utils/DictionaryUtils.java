package com.pz.vocabulary.app.utils;

import android.os.Bundle;

import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.screens.IntentArguments;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by piotr on 04/07/14.
 */
public class DictionaryUtils implements IntentArguments, Arguments {

    public static List<Word> getWordSince(Dictionary dictionary, int showWordsSince) {
        List<Word> quizWords = null;

        if (showWordsSince != -1) {
            Date since = DateUtils.getDateFromBundleArg(showWordsSince);
            quizWords = dictionary.getWordsInsertedSince(since);
        }
        return quizWords;
    }

    public static List<Word> getToughWords(Dictionary dictionary, int showWordsSince)
    {
        List<Word> quizWords = null;

        return quizWords;
    }

    public static List<Word> getWordsFromBundle(Dictionary dictionary, Bundle bundle)
    {
        long value = 0;
        String selectedAction = null;

        String[] actions = new String[] {
                SHOW_LEAST_WORDS,
                SHOW_TOP_WORDS,
                SHOW_WORD_MEANINGS,
                SHOW_WORDS_FROM_LANGUAGE
        };

        for (String action : actions)
        {
            selectedAction = action;
            value = bundle.getLong(action);
            if (value != 0)
                break;
        }

        if (selectedAction.equals(SHOW_LEAST_WORDS))
        {
            return dictionary.getLeastScoredWords((int) value);

        } else if (selectedAction.equals(SHOW_TOP_WORDS))
        {
            return dictionary.getTopScoredWords((int) value);

        } else if (selectedAction.equals(SHOW_WORD_MEANINGS))
        {
            List<Translation> meanings =  dictionary.findMeanings(value);
            List<Word> words = new ArrayList<Word>();
            for (Translation meaning : meanings)
                words.add(dictionary.findWord(meaning.getWordTo()));
            return words;
        } else if (selectedAction.equals(SHOW_WORDS_FROM_LANGUAGE))
        {
            return dictionary.findWords(value);
        }
        return null;
    }
}
