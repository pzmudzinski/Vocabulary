package com.pz.vocabulary.app.sql.ormlite;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.SQLStore;
import com.pz.vocabulary.app.utils.Logger;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piotr on 27/06/14.
 */

public class OrmLiteSQLDictionary extends SQLStore implements Dictionary {

    private OrmLiteSQLDatabaseHelper helper;
    private static final String TAG = "db";
    private OrmLiteQuizHistory quizHistory;

    public OrmLiteSQLDictionary(Context context, OrmLiteSQLDatabaseHelper helper) {
        super(context, helper);
        this.helper = helper;
        this.quizHistory = new OrmLiteQuizHistory(getDao(QuizResponse.class), getDao(Quiz.class), getDao(Word.class), getDao(Translation.class));
    }

    @Override
    public boolean hasItems(Class clz) {
        return numberOfItems(clz) > 0;
    }

    @Override
    public long numberOfItems(Class clz) {
        Dao<Object, Long> dao = getDao(clz);
        try {
            return dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long insertWord(Word word) {
        Dao<Word, Long> wordDao = helper.getDaoObject(Word.class);
        try {
            wordDao.createOrUpdate(word);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return word.getId();
    }

    @Override
    public long findWord(long langID, String spelling) {
        Dao<Word, Long> words = getDao(Word.class);

        QueryBuilder<Word, Long> queryBuilder = words.queryBuilder();
        Where<Word, Long> where = queryBuilder.where();
        try {
            SelectArg selectArg = new SelectArg(spelling);
            where.like(DBColumns.NORMALIZED_SPELLING, selectArg);
            where.and();
            where.eq(DBColumns.LANGUAGE_ID, langID);

            Word foundWord = where.queryForFirst();
            if (foundWord == null)
                return -1;
            else
                return foundWord.getId();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("db", e.getMessage(), e);
        }

        return -1;
    }

    @Override
    public Word findWord(long id) {
        Dao<Word, Long> wordDao = helper.getDaoObject(Word.class);
        try {
            return wordDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("db", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean deleteWord(long wordID) {
        Dao<Word, Long> wordDao = getDao(Word.class);

        try {
            int rows = wordDao.deleteById(wordID);
            return rows != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("db", e.getMessage(), e);
            return true;
        }
    }

    @Override
    public void addMemoryToTranslation(long memoryId, long translationId) {
        Dao<Translation, Long> translations = getDao(Translation.class);
        try {
            UpdateBuilder<Translation, Long> updateBuilder = translations.updateBuilder();
            updateBuilder.where().idEq(translationId);
            updateBuilder.updateColumnValue(DBColumns.MEMORY_ID, memoryId);
            updateBuilder.update();
        } catch (SQLException e) {
            Logger.error("db", e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    public long insertTranslation(long wordFrom, long wordTo) {
        Translation translation = new Translation(wordFrom, wordTo);
        Dao<Translation, Long> translations = getDao(Translation.class);
        try {
            translations.create(translation);
            return translation.getId();
        } catch (SQLException e) {
            Logger.error(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public long insertTranslation(long wordFrom, long wordTo, Long memoryId) {
        Dao<Translation, Long> translations = getDao(Translation.class);
        Translation translation = new Translation(wordFrom, wordTo, memoryId);
        try {
            translations.create(translation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return translation.getId();
    }

    @Override
    public void deleteTranslation(long wordFrom, long wordTo) {
        try{
            WordDao wordDao = (WordDao) getDao(Word.class);
            wordDao.deleteTranslation(wordFrom, wordTo);
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public long insertWordsAndTranslation(Word word1, Word word2, Memory memory) {
        Dao<Word, Long> words = getDao(Word.class);
        Dao<Translation, Long> translations = getDao(Translation.class);
        Dao<Memory, Long> memories = getDao(Memory.class);

        try {
            long word1SameSpelling = findWord(word1.getLanguageID(), word1.getNormalizedSpelling());
            long word2SameSpelling = findWord(word2.getLanguageID(), word2.getNormalizedSpelling());

            if (word1SameSpelling != 0)
                word1.setId(word1SameSpelling);
            if (word2SameSpelling != 0)
                word2.setId(word2SameSpelling);

            words.createOrUpdate(word1);
            words.createOrUpdate(word2);

            if (memory != null)
            {
                SelectArg selectArg = new SelectArg(memory.getDescription());
                List<Memory> memoryList = memories.queryForEq(DBColumns.DESCRIPTION, selectArg);
                Memory memoryWithSameDescription = memoryList.size() == 0? null : memoryList.get(0);
                if (memoryWithSameDescription != null)
                    memory.setId(memoryWithSameDescription.getId());
                memories.createOrUpdate(memory);
            }
            Translation translation = new Translation(word1, word2, memory);
            Translation theSameTranslation = findTranslation(word1.getId(), word2.getId());
            if (theSameTranslation != null)
            {
                translation.setId(theSameTranslation.getId());
                translation.setTimestamp(theSameTranslation.getTimestamp());
            }
            translations.createOrUpdate(translation);

            return translation.getId();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("db", e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public Translation findTranslation(long wordFrom, long wordTo) {
        try {
            return getDao(Translation.class).
                    queryBuilder()
                    .where().eq(DBColumns.WORD_FROM, wordFrom).
                            and().eq(DBColumns.WORD_TO, wordTo).
                            queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Translation> findMeanings(long wordId) {
        /* SELECT id, memory_id, CASE WHEN word_to=?
            THEN word_from
                ELSE word_to
            END FROM translations
                WHERE word_to=? OR word_from=? */

        try {
            Dao<Translation, Long> translations = getDao(Translation.class);

            QueryBuilder<Translation, Long> queryBuilder = translations.queryBuilder();
            String id = String.valueOf(wordId);
            queryBuilder.where().eq(DBColumns.WORD_FROM, id).or().eq(DBColumns.WORD_TO, id);

            List<Translation> meanings = queryBuilder.query();

            for (Translation translation : meanings)
            {
                if (translation.getTranslation().getId() == wordId)
                    translation.swap();
            }

            return meanings;
        } catch (SQLException e) {
            Logger.error("db", e.getMessage(), e);
        }
        return new ArrayList<Translation>();

    }

    @Override
    public Memory findMemory(long memoryId) {
        try {
            return getDao(Memory.class).queryForId(memoryId);
        } catch (SQLException e) {
            Logger.error("db", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public long insertMemory(Memory memory) {
        try {
            getDao(Memory.class).create(memory);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public <T> Dao<T, Long> getDao(Class<T> clz) {
        return helper.getDaoObject(clz);
    }

    @Override
    public List<Word> getAllWords() {
        try {
            Dao<Word,Long> words = helper.getDaoObject(Word.class);
            QueryBuilder<Word, Long> queryBuilder = words.queryBuilder();
            queryBuilder.selectColumns(DBColumns.ID, DBColumns.SPELLING, DBColumns.LANGUAGE_ID);
            return words.query(queryBuilder.prepare());

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<Word>();
        }
    }

    @Override
    public Language findLanguage(long id) {
        try {
            return getDao(Language.class).queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("db", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Language> getLanguages() {
        try {
            return getDao(Language.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("db", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Word> findWords(long languageId) {
        Dao<Word, Long> words = getDao(Word.class);
        try {
            return words.queryBuilder().orderBy(DBColumns.SPELLING, true).
                    selectColumns(DBColumns.ID, DBColumns.SPELLING).
                    where()
                    .eq(DBColumns.LANGUAGE_ID, languageId).
                            query();
        } catch (SQLException e) {
            Logger.error(TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception ex)
        {
            Logger.error(TAG, ex.getMessage(), ex);
        }

        return new ArrayList<Word>();
    }

    @Override
    public Map<Language, List<Word>> getWordsByLanguage() {
        List<Language> languages = getLanguages();
        Map<Language, List<Word>> map = new HashMap<Language, List<Word>>();

        for (Language language : languages)
        {
            map.put(language, findWords(language.getId()));
        }
        return map;
    }

    @Override
    public List<Word> getWordsInsertedSince(Date date) {
        Dao<Translation, Long> dao = getDao(Translation.class);

        /*
            QueryBuilder<Order, Integer> orderQb = orderDao.queryBuilder();
            orderQb.where().ge("amount", 100.0F);
            QueryBuilder<Account, Integer> accountQb = accountDao.queryBuilder();
            // join with the order query
            List<Account> results = accountQb.join(orderQb).query();
         */
        QueryBuilder<Translation, Long> translations = dao.queryBuilder();

        QueryBuilder<Word, Long> words = getDao(Word.class).queryBuilder();

        QueryBuilder<Translation, Long> from = dao.queryBuilder();
        QueryBuilder<Translation, Long> to = dao.queryBuilder();

        try {
            translations.selectColumns(DBColumns.WORD_FROM, DBColumns.WORD_TO).where().gt(DBColumns.TIMESTAMP, date);
            from.selectColumns(DBColumns.WORD_FROM).where().gt(DBColumns.TIMESTAMP, date);
            to.selectColumns(DBColumns.WORD_TO).where().gt(DBColumns.TIMESTAMP, date);

            return words.distinct().selectColumns(DBColumns.ID).where().in(DBColumns.ID, to).or().in(DBColumns.ID, from).query();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error(TAG, e.getMessage(), e);

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Word> getToughWords(Date since, float maxScore) {
        return quizHistory.getToughWords(since, maxScore);
    }

    @Override
    public void deleteResponse(long responseID) {
        quizHistory.deleteResponse(responseID);
    }

    @Override
    public QuizResponse insertResponse(long wordFrom, String response, QuizQuestionResult result) {
        return quizHistory.insertResponse(wordFrom, response, result);
    }

    @Override
    public List<QuizResponse> findResponsesWithResult(QuizQuestionResult result) {
        return quizHistory.findResponsesWithResult(result);
    }

    @Override
    public List<QuizResponse> getAllResponses() {
        return quizHistory.getAllResponses();
    }

    @Override
    public long insertQuiz(Quiz quiz) {
        return quizHistory.insertQuiz(quiz);
    }

    @Override
    public void updateResponsesWithQuiz(List<Long> responsesID, long quizID) {
        quizHistory.updateResponsesWithQuiz(responsesID, quizID);
    }

    @Override
    public long numberOfAllResponses() {
        return quizHistory.numberOfAllResponses();
    }

    @Override
    public long numberOfResponsesWithResult(QuizQuestionResult result) {
        return quizHistory.numberOfResponsesWithResult(result);
    }

    @Override
    public float quizAverageScore() {
        return quizHistory.quizAverageScore();
    }

    @Override
    public org.joda.time.Period quizTotalTimeSpent() {
        return quizHistory.quizTotalTimeSpent();
    }

    @Override
    public org.joda.time.Period quizAverageTimeSpent() {
        return quizHistory.quizAverageTimeSpent();
    }

    @Override
    public float getWordAcquaintance(long wordID) {
        return quizHistory.getWordAcquaintance(wordID);
    }

    @Override
    public void close() {
        OpenHelperManager.releaseHelper();
    }

    @Override
    public List<Memory> getAllMemories() {
        try {
            return getDao(Memory.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void destroyEverything() {
        helper.destroyEverything();
    }

    @Override
    public Date getInsertionDate(long wordID) {

        Dao<Translation, Long> translations = getDao(Translation.class);

        QueryBuilder<Translation, Long> queryBuilder = translations.queryBuilder();

        try {
            queryBuilder.
                    limit(Long.valueOf(1)).
                    selectColumns(DBColumns.TIMESTAMP).where().
                    eq(DBColumns.WORD_FROM, wordID).
                    or().
                    eq(DBColumns.WORD_TO, wordID);

            return translations.query(queryBuilder.prepare()).get(0).getTimestamp();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    @Override
    public void addLanguages(String language, String language1) {
        Dao<Language, Long> langsDao = getDao(Language.class);
        try {
            langsDao.create(new Language(language));
            langsDao.create(new Language(language1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Word> getTopScoredWords(int limit) {
        return quizHistory.getTopScoredWords(limit);
    }

    @Override
    public List<Word> getLeastScoredWords(int limit) {
        return quizHistory.getLeastScoredWords(limit);
    }
}
