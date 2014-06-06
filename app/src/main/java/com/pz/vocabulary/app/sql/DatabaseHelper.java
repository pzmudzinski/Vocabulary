package com.pz.vocabulary.app.sql;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.utils.Logger;

/**
 * Created by piotr on 14/05/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_WORDS = "words";
    public static final String TABLE_TRANSLATIONS = "translations";
    public static final String TABLE_LANGUAGES = "languages";
    public static final String TABLE_MEMORIES = "memories";

    private Resources resources;

//    public DatabaseHelper(Context context)
//    {
//        this(context, context.getResources().getString(R.string.database_name));
//    }

    public DatabaseHelper(Context context, String dbName)
    {
        super(context, dbName, new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
                Logger.log("db", "query: " + sqLiteQuery.toString());
                return new SQLiteCursor(sqLiteDatabase, sqLiteCursorDriver, s, sqLiteQuery);
            }
        }, DATABASE_VERSION);
        this.resources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createWords = resources.getString(R.string.query_create_table_words);
        String createLanguages = resources.getString(R.string.query_create_table_languages);
        String createTranslations = resources.getString(R.string.query_create_table_translations);
        String createMemories = resources.getString(R.string.query_create_table_memories);
        String[] insertLanguages = resources.getStringArray(R.array.query_insert_languages);
        db.execSQL(createLanguages);
        db.execSQL(createWords);
        db.execSQL(createTranslations);
        db.execSQL(createMemories);

        for (String insert : insertLanguages)
        {
            db.execSQL(insert);
        }
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
