package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.QuizResults;
import com.pz.vocabulary.app.utils.ColorUtils;
import com.pz.vocabulary.app.views.ScoreView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by piotr on 07/06/14.
 */
@EActivity(R.layout.activity_quiz_results)
public class QuizResultsActivity extends VocabularyActivity {

    @ViewById(R.id.textViewQuestionsCount)
    protected TextView allQuestions;
    @ViewById(R.id.textViewQuestionsCorrect)
    protected TextView correctQuestions;
    @ViewById(R.id.textViewQuestionsWrong)
    protected TextView wrongQuestions;
    @ViewById(R.id.resultsPercent)
    protected TextView percentResult;
    @ViewById(R.id.textViewQuestionsSkipped)
    protected TextView skippedQuestions;
    @ViewById(R.id.viewScore)
    protected FrameLayout scoreView;

    public static final String ARG_RESULTS = "results";


    public static void open(Context context, QuizResults results)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_RESULTS, results);
        Intent intent = QuizResultsActivity_.intent(context).get();
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    private QuizResults results;

    @AfterViews
    protected void showResults()
    {
        this.results = getIntent().getParcelableExtra(ARG_RESULTS);

        this.correctQuestions.setText(String.valueOf(results.getCorrectAnswers()));
        this.allQuestions.setText(String.valueOf(results.getQuestionsCount()));
        this.wrongQuestions.setText(String.valueOf(results.getWrongAnswers()));
        this.skippedQuestions.setText(String.valueOf(results.getSkippedAnswers()));

        String perText = ScoreView.percentText(results.getScore());
        this.percentResult.setText(perText);
        this.scoreView.setBackgroundColor(ColorUtils.getColor(1.0f - results.getScore()));
    }

    @Click(R.id.buttonQuit)
    protected void quit()
    {
        finish();
    }
}
