package com.pz.vocabulary.app.sql;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.utils.Logger;

/**
 * Created by piotr on 14/05/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_WORDS = "words";
    public static final String TABLE_TRANSLATIONS = "translations";
    public static final String TABLE_LANGUAGES = "languages";
    public static final String TABLE_MEMORIES = "memories";

    private Resources resources;

    public DatabaseHelper(Context context)
    {
        this(context, context.getResources().getString(R.string.database_name));
    }

    public DatabaseHelper(Context context, String dbName)
    {
        super(context, dbName, null, DATABASE_VERSION);
        this.resources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createWords = resources.getString(R.string.query_create_table_words);
        String createLanguages = resources.getString(R.string.query_create_table_languages);
        String createTranslations = resources.getString(R.string.query_create_table_translations);
        String createMemories = resources.getString(R.string.query_create_table_memories);
        String insertLanguages = resources.getString(R.string.query_insert_languages);
        db.execSQL(createLanguages);
        db.execSQL(createWords);
        db.execSQL(createTranslations);
        db.execSQL(createMemories);
        db.execSQL(insertLanguages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.log("db",
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);

        onCreate(db);
    }
}
