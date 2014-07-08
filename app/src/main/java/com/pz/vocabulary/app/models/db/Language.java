package com.pz.vocabulary.app.models.db;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;

/**
 * Created by piotr on 04/06/14.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_LANGUAGES)
public class Language extends BaseEntity {

    @Deprecated
    public static final long POLISH = 1;
    @Deprecated
    public static final long ENGLISH = 2;


    @DatabaseField
    private String name;

    public Language(String name) {
        this.name = name;
    }

    public Language(String name, String prettyName)
    {
        this.name = name;
        //@TODO pretty name
    }

    public Word newWord(String spelling)
    {
        Word word = new Word(this, spelling);
        return word;
    }

    public Language()
    {
        super();
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Language))
            return false;

        Language other = (Language) o;
        if (other == null)
            return false;

        return other.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
