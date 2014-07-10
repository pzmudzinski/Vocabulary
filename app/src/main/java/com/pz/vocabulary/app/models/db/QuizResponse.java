package com.pz.vocabulary.app.models.db;

import android.database.Cursor;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;
import com.pz.vocabulary.app.sql.Dictionary;

import java.util.Date;

/**
 * Created by piotr on 11.06.2014.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_RESPONSES)
public class QuizResponse extends BaseEntity{

    @DatabaseField(foreign = true, foreignColumnName = DBColumns.ID, columnName = DBColumns.WORD_FROM)
    private Word wordFrom;
    @DatabaseField
    private String response;
    @DatabaseField(dataType = DataType.ENUM_INTEGER)
    private Dictionary.QuizQuestionResult result;
    @DatabaseField(version = true, columnName = DBColumns.TIMESTAMP, dataType = DataType.DATE)
    private Date timestamp;

    public long getQuizID() {
        return quizID;
    }

    @DatabaseField(columnName = DBColumns.QUIZ_ID)//, columnDefinition = "INTEGER FOREIGN KEY REFERENCES " +DatabaseTables.TABLE_QUIZZES + "("+DBColumns.ID+")")
    private long quizID;

    public QuizResponse(long wordFrom, String response, Dictionary.QuizQuestionResult result)
    {
        super();
        Word word = new Word();
        word.setId(wordFrom);
        this.wordFrom = word;
        this.response = response;
        this.result = result;
    }

    public QuizResponse()
    {
        super();
    }

    @Deprecated
    public static QuizResponse fromQuery(Cursor query, Dictionary dictionary)
    {
//        QuizResponse response = new QuizResponse();
//        response.response = query.getString(query.getColumnIndex(DBColumns.RESPONSE));
//        response.result = Dictionary.QuizQuestionResult.values()[query.getInt(query.getColumnIndex(DBColumns.RESULT))];
//        long wordToId = query.getLong(query.getColumnIndex(DBColumns.WORD_TO));
//        response.wordTo = dictionary.findWord(wordToId);
//
//        long ts = query.getInt(query.getColumnIndex(DBColumns.TIMESTAMP));
//        response.date = new Date(ts);
//        return response;
        return null;
    }

    public Word getWordFrom() {
        return wordFrom;
    }

    public String getResponse() {
        return response;
    }

    public Dictionary.QuizQuestionResult getResult() {
        return result;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
