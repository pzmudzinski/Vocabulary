package com.pz.vocabulary.app.sql.ormlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.QuizHistory;
import com.pz.vocabulary.app.utils.Logger;

import org.joda.time.Period;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by piotr on 03/07/14.
 */
public class OrmLiteQuizHistory implements QuizHistory {
    Dao<QuizResponse,Long> dao;
    Dao<Quiz, Long> quizDao;

    public OrmLiteQuizHistory(Dao<QuizResponse, Long> dao, Dao<Quiz, Long> quizDao)
    {
        this.dao = dao;
        this.quizDao = quizDao;
    }

    @Override
    public long insertResponse(long wordFrom, String response, QuizQuestionResult result) {

        QuizResponse quizResponse = new QuizResponse(wordFrom, response, result);
        try {
            dao.create(quizResponse);
            return quizResponse.getId();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<QuizResponse> findResponsesWithResult(QuizQuestionResult result) {

        try {
            return dao.queryForEq(DBColumns.RESULT, result);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<QuizResponse> getAllResponses() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public long insertQuiz(Quiz quiz) {
        try {
            return quizDao.create(quiz);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return -1;
        }

    }

    @Override
    public void updateResponsesWithQuiz(List<Long> responsesID, long quizID) {
        UpdateBuilder<QuizResponse, Long> update = dao.updateBuilder();
        try {
            for (Long responseID : responsesID)
            {
                update.updateColumnValue(DBColumns.QUIZ_ID, quizID).where().idEq(responseID);
                dao.update(update.prepare());
            }

        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public long numberOfAllResponses() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long numberOfResponsesWithResult(QuizQuestionResult result) {
        QueryBuilder<QuizResponse, Long> queryBuilder = dao.queryBuilder();
        try {
            queryBuilder.setCountOf(true).where().eq(DBColumns.RESULT, result);
            return dao.countOf(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    @Override
    public float quizAverageScore() {
        //quizDao.queryBuilder().sele
        /*
        // select 2 aggregate functions as the return
            qb.selectRaw("MIN(orderCount)", "MAX(orderCount)");
// the results will contain 2 string values for the min and max
            results = accountDao.queryRaw(qb.prepareStatementString());
            String[] values = results.getFirstResult();
         */
        QueryBuilder<Quiz, Long> queryBuilder = quizDao.queryBuilder();

        queryBuilder.selectRaw("AVG("+ DBColumns.RESULT + ")");
        try {
            GenericRawResults<String[]> results = quizDao.queryRaw(queryBuilder.prepareStatementString());
            String[] asStrings = results.getFirstResult();
            if (asStrings == null || asStrings[0] == null)
                return 0;
            return Float.parseFloat(asStrings[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("db", e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public Period quizTotalTimeSpent() {
        QueryBuilder<Quiz, Long> queryBuilder = quizDao.queryBuilder();
        //queryBuilder.selectRaw()
        queryBuilder.selectRaw("(SUM("+DBColumns.TIMESTAMP_END+")-SUM("+DBColumns.TIMESTAMP_START+"))");

        try {
            long period = quizDao.queryRawValue(queryBuilder.prepareStatementString());
            Period jodaPeriod = new Period(period);
            return jodaPeriod;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.error("db", ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public Period quizAverageTimeSpent()
    {
        QueryBuilder<Quiz, Long> queryBuilder = quizDao.queryBuilder();
        //queryBuilder.selectRaw()
        queryBuilder.selectRaw("AVG("+DBColumns.TIMESTAMP_END+"-"+DBColumns.TIMESTAMP_START+")");

        try {
            long period = quizDao.queryRawValue(queryBuilder.prepareStatementString());
            Period jodaPeriod = new Period(period);
            return jodaPeriod;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.error("db", ex.getMessage(), ex);
            return null;
        }
    }
}
