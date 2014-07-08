package com.pz.vocabulary.app.sql;

import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.QuizResponse;

import java.util.List;



/**
 * Created by piotr on 03/07/14.
 */
public interface QuizHistory {
    public long insertResponse(long wordFrom, String response, QuizQuestionResult result);
    public List<QuizResponse> findResponsesWithResult(QuizQuestionResult result);
    public List<QuizResponse> getAllResponses();

    public long insertQuiz(Quiz quiz);
    public void updateResponsesWithQuiz(List<Long> responsesID, long quizID);

    public long numberOfAllResponses();
    public long numberOfResponsesWithResult(QuizQuestionResult result);

    public float quizAverageScore();
    public org.joda.time.Period quizTotalTimeSpent();
    public org.joda.time.Period quizAverageTimeSpent();

    public enum QuizQuestionResult
    {
        ResponseCorrect,
        ResponseWrong,
        ResponseSkipped
    }
}
