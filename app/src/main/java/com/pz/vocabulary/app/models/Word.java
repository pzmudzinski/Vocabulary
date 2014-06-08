package com.pz.vocabulary.app.models;

import android.database.Cursor;

import com.pz.vocabulary.app.sql.DBColumns;

import java.text.Normalizer;

/**
 * Created by piotr on 04/06/14.
 */
public class Word extends BaseEntity{
    private String spelling;
    private Language language;

    public static Word fromCursor(Cursor query, Language language)
    {
        Word word = new Word();

        word.id = query.getLong(query.getColumnIndex(DBColumns.ID));
        word.spelling = query.getString(query.getColumnIndex(DBColumns.SPELLING));
        word.language = language;

        return word;
    }

    private Word()
    {

    }

    public Word(Language language, String spelling)
    {
        super();
        this.language = language;
        this.spelling = spelling;
    }

    public String getNormalizedSpelling()
    {
        String asciiName = Normalizer.normalize(spelling, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return asciiName;
    }

    public String getSpelling()
    {
        return spelling;
    }

    public long getLanguageID()
    {
        return language.getId();
    }

    public Language getLanguage()
    {
        return language;
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
        return "[lang:"+language+"/id:"+id+"]"+" " + spelling + " ("+getNormalizedSpelling()+")";
    }
}
