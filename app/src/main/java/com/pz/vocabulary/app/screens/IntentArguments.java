package com.pz.vocabulary.app.screens;

/**
 * Created by piotr on 04/07/14.
 */
public interface IntentArguments {
    public static final int ARG_VALUE_WORDS_SINCE_NOT_DEFINIED = 255;
    public static final int ARG_VALUE_WORDS_SINCE_TODAY = 0;
    public static final int ARG_VALUE_WORDS_SINCE_YESTERDAY = 1;
    public static final int ARG_VALUE_WORDS_SINCE_3_DAYS = 2;
    public static final int ARG_VALUE_WORDS_SINCE_WEEK = 3;
    public static final int ARG_VALUE_WORDS_SINCE_MONTH = 4;
    public static final int ARG_VALUE_WORDS_SINCE_3_MONTHS = 5;
    public static final int ARG_VALUE_WORDS_SINCE_6_MONTHS = 6;
    public static final int ARG_VALUE_WORDS_SINCE_12_MONTHS = 7;

    public static final String ARG_WORD_IDS = "arg_word_ids";
}
