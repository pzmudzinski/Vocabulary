package com.pz.vocabulary.app.utils;

import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.screens.IntentArguments;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.Date;
import java.util.List;

/**
 * Created by piotr on 04/07/14.
 */
public class DictionaryUtils implements IntentArguments {

    public static List<Word> getWordSince(Dictionary dictionary, int showWordsSince) {
        List<Word> quizWords = null;

        if (showWordsSince != -1) {
            Date since = null;
            switch (showWordsSince) {
                case ARG_VALUE_WORDS_SINCE_TODAY:
                    since = DateUtils.today();
                    break;
                case ARG_VALUE_WORDS_SINCE_YESTERDAY:
                    since = DateUtils.todayMinusXDays(1);
                    break;
                case ARG_VALUE_WORDS_SINCE_3_DAYS:
                    since = DateUtils.todayMinusXDays(3);
                    break;
                case ARG_VALUE_WORDS_SINCE_WEEK:
                    since = DateUtils.startWeek();
                    break;
                case ARG_VALUE_WORDS_SINCE_MONTH:
                    since = DateUtils.startMonth();
                    break;
            }

            quizWords = dictionary.getWordsInsertedSince(since);
        }
        return quizWords;
    }
}
