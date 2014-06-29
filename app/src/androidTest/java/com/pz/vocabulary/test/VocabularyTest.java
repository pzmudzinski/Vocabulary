package com.pz.vocabulary.test;

import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDatabaseHelper;
import com.pz.vocabulary.app.sql.ormlite.OrmLiteSQLDictionary;

/**
 * Created by piotr on 07/06/14.
 */
public class VocabularyTest extends AndroidTestCase {

    protected Dictionary dbStore;
    protected SQLiteOpenHelper dbHelper;

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
        this.dbHelper = OpenHelperManager.getHelper(getContext(), OrmLiteSQLDatabaseHelper.class);

        this.dbStore = new OrmLiteSQLDictionary(getContext(), (OrmLiteSQLDatabaseHelper) dbHelper);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbStore.close();
        OpenHelperManager.releaseHelper();
    }
}
