package com.pz.vocabulary.app.sql.ormlite;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DatabaseTables;
import com.pz.vocabulary.app.utils.Logger;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 27/06/14.
 */
public class OrmLiteSQLDatabaseHelper extends OrmLiteSqliteOpenHelper implements DatabaseTables {

    private Map<Class, Dao<Class, Long>> daos = new HashMap<Class, Dao<Class, Long>>();

    public OrmLiteSQLDatabaseHelper(Context context){
        super(context,context.getString(R.string.database_name), null, DATABASE_VERSION );
        daos.put(Memory.class, null);
        daos.put(Language.class, null);
        daos.put(Word.class, null);
        daos.put(Translation.class, null);
        daos.put(QuizResponse.class, null);
        daos.put(Quiz.class, null);
        getWritableDatabase();
    }

    public OrmLiteSQLDatabaseHelper(Context context, String dbName)
    {
        super(context, dbName, new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
                Logger.log("db", "query: " + sqLiteQuery.toString());
                return new SQLiteCursor(sqLiteDatabase, sqLiteCursorDriver, s, sqLiteQuery);
            }
        }, DATABASE_VERSION);
        daos.put(Memory.class, null);
        daos.put(Language.class, null);
        daos.put(Word.class, null);
        daos.put(Translation.class, null);
        daos.put(QuizResponse.class, null);
        daos.put(Quiz.class, null);

    }

    private OrmLiteSQLDatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    private OrmLiteSQLDatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    private OrmLiteSQLDatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, File configFile) {
        super(context, databaseName, factory, databaseVersion, configFile);
    }

    private OrmLiteSQLDatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, InputStream stream) {
        super(context, databaseName, factory, databaseVersion, stream);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
       recreate(connectionSource);
    }

    public <T> Dao<T,Long> getDaoObject(Class<T> clz)
    {
        if (daos.get(clz) == null)
        {
            try {
                daos.put(clz, (Dao<Class, Long>) getDao(clz));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return (Dao<T, Long>) daos.get(clz);
    }

    private WordDao wordDao;

    @Override
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        if (clazz.equals(Word.class))
        {
            if (this.wordDao == null)
            {
                Dao<Translation, Long> translations = getDao(Translation.class);
                Dao<Memory, Long> memories = getDao(Memory.class);
                this.wordDao = new WordDao(getConnectionSource(), translations, memories, getDaoObject(QuizResponse.class));
            }
            return (D) wordDao;
        }
        return super.getDao(clazz);
    }

    public void destroyEverything() {
        for (Map.Entry<Class, Dao<Class, Long>> entry : daos.entrySet())
        {
            Class clz = entry.getKey();
            if (clz.equals(Language.class))
                continue;

            try {
                TableUtils.clearTable(getConnectionSource(), clz);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void recreate(ConnectionSource connectionSource)
    {
        // create tables
        try {
            TableUtils.createTable(connectionSource, Language.class);
            TableUtils.createTable(connectionSource, Memory.class);
            TableUtils.createTable(connectionSource, Quiz.class);
            TableUtils.createTable(connectionSource, QuizResponse.class);
            TableUtils.createTable(connectionSource, Word.class);
            TableUtils.createTable(connectionSource, Translation.class);

        } catch (SQLException e) {
            Logger.error("db", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        // create initial languages

        for (Map.Entry<Class, Dao<Class, Long>> entry : daos.entrySet())
        {
            Class clz = entry.getKey();
            try {
                entry.setValue(getDao(clz));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * http://www.greenmoonsoftware.com/2012/02/sqlite-schema-migration-in-android/
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.d("db", String.format("onUpgrade: %d => %d", oldVersion, newVersion));
        if (REVISIONS.length < 1) {
            return;
        }
        for (int i = oldVersion; i < newVersion; i++) {
            try {
                Revision revision = REVISIONS[i - 1];
                revision.execute(this, connectionSource);
            } catch (Exception e) {
                Log.d("db", "Version up error", e);
                throw new RuntimeException(e);
            }
        }
        onCreate(database, connectionSource);
    }

    private static interface Revision {
        public void execute(OrmLiteSQLDatabaseHelper openHelper, ConnectionSource connectionSource)
                throws SQLException;
    }

    /**
     * Don't include "CREATE TABLE" here
     */
    private static final Revision[] REVISIONS = new Revision[]{
// Example
//            new Revision() {
//                @Override
//                public void execute(DatabaseOpenHelper openHelper, ConnectionSource connectionSource)
//                        throws SQLException {
//                    openHelper.getDao(Card.class).executeRaw(String.format(
//                            "ALTER TABLE card ADD COLUMN %s VARCHAR",
//                            Card.Column.DICTIONARY));
//                }
//            }
    };
}
