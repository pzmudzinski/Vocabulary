package com.pz.vocabulary.test;

import android.test.AndroidTestCase;

import com.pz.vocabulary.app.models.Language;
import com.pz.vocabulary.app.models.Memory;
import com.pz.vocabulary.app.models.Word;
import com.pz.vocabulary.app.sql.DatabaseHelper;
import com.pz.vocabulary.app.sql.SQLDictionary;

/**
 * Created by piotr on 07/06/14.
 */
public class VocabularyTest extends AndroidTestCase {

    protected SQLDictionary dbStore;
    protected DatabaseHelper dbHelper;

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
        this.dbHelper = new DatabaseHelper(getContext(), null);
        this.dbStore = new SQLDictionary(getContext(), dbHelper);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbStore.close();
    }
}
