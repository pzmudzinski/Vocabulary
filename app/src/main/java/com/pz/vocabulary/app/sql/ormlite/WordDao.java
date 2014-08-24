package com.pz.vocabulary.app.sql.ormlite;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;

import java.sql.SQLException;

/**
 * Created by piotr on 07/07/14.
 */
public class WordDao extends BaseDaoImpl<Word,Long> {

    Dao<Translation, Long> translationDao;
    Dao<Memory, Long> memoryDao;
    Dao<QuizResponse, Long> responseDao;

    public WordDao(ConnectionSource connectionSource, Dao<Translation, Long>
            translations, Dao<Memory, Long> memories, Dao<QuizResponse, Long> responseDao) throws SQLException {
        super(connectionSource, Word.class);
        this.translationDao = translations;
        this.memoryDao = memories;
        this.responseDao = responseDao;
        if (memoryDao == null || translations == null)
            throw new RuntimeException("Can't pass null daos to WordDao");
    }

    @Override
    public int deleteById(Long aLong) throws SQLException {
        int rows = super.deleteById(aLong);
        // find related translation
        DeleteBuilder<Translation, Long> translationDeleteBuilder = translationDao.deleteBuilder();
        translationDeleteBuilder.where().eq(DBColumns.WORD_TO, aLong).or().eq(DBColumns.WORD_FROM, aLong);

        // delete translations where word_to = id or word_from = id
        translationDao.delete(translationDeleteBuilder.prepare());

        DeleteBuilder<Word, Long> wordDeleteBuilder = this.deleteBuilder();

        /*
        DELETE FROM words
        WHERE
        (
            (SELECT count(1) FROM translations WHERE word_from = words.id OR word_to = words.id) < 1
        )
         */
        wordDeleteBuilder.where().raw(
                "( SELECT count(1) FROM " + DatabaseTables.TABLE_TRANSLATIONS +
                        " WHERE " + DBColumns.WORD_FROM +" = " + DatabaseTables.TABLE_WORDS + "." + DBColumns.ID +
                        " OR " +
                        DBColumns.WORD_TO + " = " + DatabaseTables.TABLE_WORDS + "." + DBColumns.ID + ") < 1"
        );


        this.delete(wordDeleteBuilder.prepare());

        DeleteBuilder<Memory, Long> memoryDeleteBuilder = memoryDao.deleteBuilder();

        memoryDeleteBuilder.where().not().exists(translationDao.queryBuilder().selectColumns(DBColumns.MEMORY_ID));

        memoryDao.delete(memoryDeleteBuilder.prepare());

        DeleteBuilder<QuizResponse, Long> responseDeleteBuilder = responseDao.deleteBuilder();
        responseDeleteBuilder.where().raw(
                "( SELECT count(1) FROM " + DatabaseTables.TABLE_WORDS +
                        " WHERE " + DBColumns.WORD_FROM +" = " + DatabaseTables.TABLE_WORDS + "." + DBColumns.ID +  ") < 1"
        );
        responseDao.delete(responseDeleteBuilder.prepare());
        return rows;
    }

    protected WordDao(Class<Word> dataClass) throws SQLException {
        super(dataClass);
    }

    protected WordDao(ConnectionSource connectionSource, Class<Word> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    protected WordDao(ConnectionSource connectionSource, DatabaseTableConfig<Word> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }
}
