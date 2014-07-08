package com.pz.vocabulary.app.sql;

/**
 * Created by piotr on 27/06/14.
 */
public interface DatabaseTables {

    public static final int DATABASE_VERSION = 4;

    public static final String TABLE_WORDS = "words";
    public static final String TABLE_TRANSLATIONS = "translations";
    public static final String TABLE_LANGUAGES = "languages";
    public static final String TABLE_MEMORIES = "memories";
    public static final String TABLE_RESPONSES = "responses";
    public static final String TABLE_QUIZZES = "quizzes";
}
