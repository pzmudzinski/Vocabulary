package com.pz.vocabulary.app.utils;

/**
 * Created by piotr on 08/06/14.
 */
public interface Arguments {
    public static final String ARG_WORD_MEANINGS = "arg_word_meanings";

    public static final String ARG_TITLES = "arg_titles";
    public static final String ARG_LIST_TITLE = "arg_list_title";
    public static final String ARG_WORD_TYPES = "arg_word_types";
    public static final String ARG_WORD_IDS = "arg_word_ids";

    public static final int ARG_REQUEST_SETTIGNS = 0;

    public static final int INTENT_RESULT_NOTHING = 0;
    public static final int INTENT_RESULT_DELETE = 1;

    public static final String SHOW_WORDS_FROM_LANGUAGE = "show_words_from_language";
    public static final String SHOW_WORD_MEANINGS = "show_word_meanings";
    public static final String SHOW_TOP_WORDS = "show_top_words";
    public static final String SHOW_LEAST_WORDS = "show_least_words";

    public static final int AD_SHOW_DELAY = 5000;
    public static final String AD_PREFS = "ad_prefs";
    public static final String AD_FIRST_TIME = "ad_first_time";
}
