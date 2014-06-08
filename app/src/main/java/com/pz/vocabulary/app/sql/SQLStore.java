package com.pz.vocabulary.app.sql;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by piotr on 06/06/14.
 */
public abstract class SQLStore {

    protected SQLiteDatabase db;
    protected DatabaseHelper databaseHelper;
    protected Resources resources;

    public SQLStore(Context context, DatabaseHelper helper)
    {
        this.databaseHelper = helper;
        this.db = helper.getWritableDatabase();
        this.resources = context.getResources();
    }

    public void close()
    {
        databaseHelper.close();
    }

    protected Cursor getSelectQuery(String tableName, String columnName, String value)
    {
        Cursor query = db.query(tableName, null, columnName+"=?", new String[] { value}, null, null, null);
        return query;
    }

    protected Cursor getSelectQuery(String tableName, long id)
    {
        Cursor query = db.query(tableName, null, DBColumns.ID+"=?", new String[] { String.valueOf(id)}, null, null, null);
        return query;
    }

    protected Cursor getSelectQuery(String tableName)
    {
        Cursor query = db.query(tableName, null, null, null, null, null, null);
        return query;
    }
}
