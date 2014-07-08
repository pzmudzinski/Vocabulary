package com.pz.vocabulary.app.screens;

/**
 * Created by piotr on 04/07/14.
 */
public interface IntentArguments {
    public static final String ARG_QUIZ_WORDS_SINCE = "quiz_words_since";
    public static final String ARG_QUIZ_WORDS_ALL = "quiz_words_all";
    public static final String ARG_QUIZ_WORDS_TOUGH = "quiz_words_tough";

    public static final int ARG_VALUE_WORDS_SINCE_TODAY = 0;
    public static final int ARG_VALUE_WORDS_SINCE_YESTERDAY = 1;
    public static final int ARG_VALUE_WORDS_SINCE_3_DAYS = 2;
    public static final int ARG_VALUE_WORDS_SINCE_WEEK = 3;
    public static final int ARG_VALUE_WORDS_SINCE_MONTH = 4;
}
