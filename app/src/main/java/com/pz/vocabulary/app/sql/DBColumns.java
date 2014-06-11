package com.pz.vocabulary.app.sql;

import android.provider.BaseColumns;

/**
 * Created by piotr on 14/05/14.
 */
public interface DBColumns extends BaseColumns {
    public static final String SPELLING = "spelling";
    public static final String LANGUAGE_ID = "language_id";
    public static final String WORD_FROM = "word_from";
    public static final String WORD_TO = "word_to";
    public static final String MEMORY_ID = "memory_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ID = "id";
    public static final String NORMALIZED_SPELLING = "normalized_spelling";
    public static final String RESULT = "result";
    public static final String RESPONSE = "response";
    public static final String TIMESTAMP = "ts";
}
