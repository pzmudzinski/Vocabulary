package com.pz.vocabulary.test;

import android.test.AndroidTestCase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDatabaseHelper;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDictionary;

import java.sql.SQLException;

/**
 * Created by piotr on 07/06/14.
 */
public class VocabularyTest extends AndroidTestCase {

    protected Dictionary dbStore;
    protected OrmLiteSQLDatabaseHelper dbHelper;

    protected Language polish = new Language(Language.POLISH, "pl");
    protected Language english = new Language(Language.ENGLISH, "eng");
    protected Word polishHome = new Word(polish, "dom");
    protected Word englishHome = new Word(english, "house");
    protected Word polishKey = new Word(polish, "klucz");
    protected Word polishImportant = new Word(polish, "ważny");
    protected Word englishKey = new Word(english, "key");
    protected Memory memory = new Memory("hi-hi-hi");
    protected Memory memory2 = new Memory("hi-hi-hi2");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.dbHelper = new OrmLiteSQLDatabaseHelper(getContext(), null);

        this.dbStore = new OrmLiteSQLDictionary(getContext(), (OrmLiteSQLDatabaseHelper) dbHelper);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbHelper.close();

    }
    protected String answerFor(Question question)
    {
        Word answer = dbStore.findWord(dbStore.findMeanings(question.getWord().getId()).get(0).getWordTo());
        return answer.getSpelling();
    }

    protected void updateTranslation(Translation translation)
    {
        Dao<Translation, Long> translations = null;
        try {
            translations = dbHelper.getDao(Translation.class);
            UpdateBuilder<Translation, Long> updateBuilder = translations.updateBuilder();
            updateBuilder.updateColumnValue(DBColumns.TIMESTAMP, translation.getTimestamp());
            updateBuilder.where().eq(DBColumns.ID, translation.getId());

            String query = updateBuilder.prepareStatementString();
            translations.update(updateBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
