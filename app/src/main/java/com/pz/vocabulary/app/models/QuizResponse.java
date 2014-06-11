package com.pz.vocabulary.app.models;

import android.database.Cursor;

import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.Date;

/**
 * Created by piotr on 11.06.2014.
 */
public class QuizResponse extends BaseEntity{
    public Word wordTo;
    public String response;
    public Dictionary.QuizQuestionResult result;
    public Date date;

    public static QuizResponse fromQuery(Cursor query, Dictionary dictionary)
    {
        QuizResponse response = new QuizResponse();
        response.response = query.getString(query.getColumnIndex(DBColumns.RESPONSE));
        response.result = Dictionary.QuizQuestionResult.values()[query.getInt(query.getColumnIndex(DBColumns.RESULT))];
        long wordToId = query.getLong(query.getColumnIndex(DBColumns.WORD_TO));
        response.wordTo = dictionary.findWord(wordToId);

        long ts = query.getInt(query.getColumnIndex(DBColumns.TIMESTAMP));
        response.date = new Date(ts);
        return response;
    }
}
