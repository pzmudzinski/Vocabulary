package com.pz.vocabulary.test;

import android.test.suitebuilder.annotation.Suppress;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.GenericRawResults;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.utils.Logger;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by piotr on 08/07/14.
 */
@Suppress
public class FunTests extends VocabularyTest {

    public static final String TAG = "fun";

    public static String[] generateRandomWords(int numberOfWords)
    {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }

    final int WORDS_COUNT = 1000;
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String[] randoms1 = generateRandomWords(WORDS_COUNT);
        String[] randoms2 = generateRandomWords(WORDS_COUNT);

        long start = System.currentTimeMillis();
        for (int i = 0 ; i < WORDS_COUNT ; i++)
        {
            dbStore.insertWordsAndTranslation(new Word(polish, randoms1[i]), new Word(english, randoms2[i]), null);
        }
    }

    private long start;

    public void testGettingAllWords() throws SQLException {
        start = System.currentTimeMillis();
        List<Word> words = dbStore.getAllWords();

        Logger.log(TAG, "querying takes " +  getPeriod(start));

    }

    public void testGettingMap()
    {
        start = System.currentTimeMillis();

        Map<Language, List<Word>> map = dbStore.getWordsByLanguage();

        Logger.log(TAG, "getting lang-word map " + getPeriod(start));
    }

    public void testGettingWordsWithIterator()
    {
        start = System.currentTimeMillis();

        CloseableIterator<Word> wordCloseableIterator = dbHelper.getDaoObject(Word.class).iterator();

        Logger.log(TAG, "getting closeable iterator " +  getPeriod(start) );

        try {
            wordCloseableIterator.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.log(TAG, e.getMessage());
        }
    }

    public void testGettingWordOneByOne() throws SQLException {
        start = System.currentTimeMillis();
        List<Word> ids = dbHelper.getDao(Word.class).queryBuilder().selectColumns(DBColumns.ID).query();
        Logger.log(TAG, "word one-by-one " + getPeriod(start));
    }

    public void testGettingIdsOneByOne() throws SQLException {
        start = System.currentTimeMillis();
        GenericRawResults<String[]> ids = dbHelper.getDao(Word.class).queryBuilder().selectColumns(DBColumns.ID).queryRaw();
        Iterator<String[]> closeableIterator = ids.iterator();


        List<Long> idsLong = new LinkedList<Long>();
        String[] next;

        while (closeableIterator.hasNext())
        {
            next = closeableIterator.next();
            idsLong.add(Long.parseLong(next[0]));
        }

//        try {
//            closeableIterator.close();
//        } catch (SQLiteException exception)
//        {
//            Logger.log(TAG, "ex in ids one-by-one " + exception.getMessage());
//        }


        Logger.log(TAG, "word one-by-one-ids " + getPeriod(start));
    }

    public void testGettingMeanings()
    {
        start = System.currentTimeMillis();
        List<Translation> translations = dbStore.findMeanings(543);
        assertTrue(translations.size() > 0);
        Logger.log(TAG, "find meanings " + getPeriod(start));
    }

    private String getPeriod(long since)
    {
        return new Period(System.currentTimeMillis() - since).toString(PeriodFormat.getDefault());
    }
}
