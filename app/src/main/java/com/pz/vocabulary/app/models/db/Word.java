package com.pz.vocabulary.app.models.db;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;

import java.text.Normalizer;

/**
 * Created by piotr on 04/06/14.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_WORDS)
public class Word extends BaseEntity{
    @DatabaseField(canBeNull = false)
    private String spelling;

    @DatabaseField(columnName = DBColumns.NORMALIZED_SPELLING, canBeNull = false)
    private String normalizedSpelling;

    @DatabaseField(columnName = DBColumns.LANGUAGE_ID)
    private long languageID;

    @Deprecated
    public static Word fromCursor(Cursor query, Language language)
    {
        Word word = new Word();

        word.id = query.getLong(query.getColumnIndex(DBColumns.ID));
        word.spelling = query.getString(query.getColumnIndex(DBColumns.SPELLING));
        word.languageID = language.getId();

        return word;
    }

    public Word()
    {

    }

    public Word(long languageID, String spelling)
    {
        super();
        this.languageID = languageID;
        setSpelling(spelling);
    }

    @Deprecated
    public Word(Language language, String spelling) {
        super();
        this.languageID = language.getId();
        setSpelling(spelling);
    }

    public String getNormalizedSpelling()
    {
        return normalizedSpelling;
    }

    public String getSpelling()
    {
        return spelling;
    }

    public long getLanguageID()
    {
        return languageID;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != o.getClass())
            return false;

        Word other = (Word) o;

        return other.getLanguageID() == this.getLanguageID() && other.getNormalizedSpelling().equals(this.getNormalizedSpelling());
    }

    @Override
    public String toString() {
        return spelling;
    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
        String asciiName = Normalizer.normalize(spelling, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        this.normalizedSpelling = asciiName;
    }

}
