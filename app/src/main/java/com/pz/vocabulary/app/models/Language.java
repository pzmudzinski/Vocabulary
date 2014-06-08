package com.pz.vocabulary.app.models;

import android.database.Cursor;

import com.pz.vocabulary.app.sql.DBColumns;

/**
 * Created by piotr on 04/06/14.
 */
public class Language extends BaseEntity {

    public static final long POLISH = 1;
    public static final long ENGLISH = 2;

    private String name;

    public Word newWord(String spelling)
    {
        Word word = new Word(this, spelling);
        return word;
    }

    public static Language fromCursor(Cursor query)
    {
        return new Language(query.getLong(query.getColumnIndex(DBColumns.ID)), query.getString(query.getColumnIndex(DBColumns.NAME)));
    }

    public Language(long id, String name)
    {
        super(id);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
