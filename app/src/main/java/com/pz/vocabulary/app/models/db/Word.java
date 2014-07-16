package com.pz.vocabulary.app.models.db;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;

import java.text.Normalizer;

/**
 * Created by piotr on 04/06/14.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_WORDS)
public class Word extends BaseEntity implements Parcelable {
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

        return other.getLanguageID() == this.getLanguageID() && other.getNormalizedSpelling().equalsIgnoreCase(this.getNormalizedSpelling());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.spelling);
    }

    private Word(Parcel in) {
        this.id = in.readLong();
        this.spelling = in.readString();
    }

    public static Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public static Word[] toWords(Parcelable[] parcelables) {
        Word[] objects = new Word[parcelables.length];
        System.arraycopy(parcelables, 0, objects, 0, parcelables.length);
        return objects;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    private float score;
}
