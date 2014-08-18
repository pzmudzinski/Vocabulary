package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.AlertUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;
import java.util.List;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity(R.layout.activity_quiz)
public class QuizActivity extends VocabularyActionBarActivity implements IntentArguments {
    @ViewById(R.id.editTextAnswer)
    protected EditText editTextAnswer;
    @ViewById(R.id.textViewQuestion)
    protected TextView textViewQuestion;
    @ViewById(R.id.buttonAnswer)
    protected Button answerButton;
    @ViewById(R.id.textViewTip)
    protected TextView textViewTip;

    private Quiz quiz;

    public static void open(Context context, List<Word> words)
    {
        Intent intent = QuizActivity_.intent(context).get();
        Word[] wordsAsArray = words.toArray(new Word[words.size()]);
        intent.putExtra(ARG_WORD_IDS, wordsAsArray);
       // intent.getExtras().putParcelableArray(ARG_WORD_IDS, Word.CREATOR);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @AfterViews
    protected void init() {
        Parcelable[] parcelables = getIntent().getParcelableArrayExtra(ARG_WORD_IDS);
        Word[] words = Arrays.copyOf(parcelables, parcelables.length, Word[].class);;

        getSupportActionBar().setTitle("");

        this.quiz = new Quiz(getDictionary(), Arrays.asList(words));
        answerButton.setOnClickListener(buttonAnswerClick);

        takeNextQuestionOrGoToResults();
    }
    private Question currentQuestion;

    private void display(Question question) {
        this.currentQuestion = question;
        Word word = question.getWord();
        textViewQuestion.setText(word.getSpelling());
        editTextAnswer.setText("");

        if (question.getMemory() == null) {
            textViewTip.setText(getString(R.string.answer_no_tip));
        } else {
            textViewTip.setText(String.format(getString(R.string.answer_tip), question.getMemory().getDescription()));
        }
    }

    final View.OnClickListener buttonAnswerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onAnswerClicked();
        }
    };

    protected synchronized void onAnswerClicked() {
        boolean correct = quiz.answer(editTextAnswer.getText().toString());
        if (correct)
            onCorrectAnswer();
        else
            onWrongAnswer();
    }

    @Click(R.id.buttonTakeNext)
    protected void onTakeNextQuestion() {
        quiz.skipQuestion();
        takeNextQuestionOrGoToResults();
    }

    @AfterTextChange(R.id.editTextAnswer)
    protected void onTextChanged()
    {
        answerButton.setEnabled(editTextAnswer.getText().length() > 0);
    }

    private synchronized void takeNextQuestionOrGoToResults() {
        answerButton.setOnClickListener(null);
        answerButton.setEnabled(false);
        if (quiz.hasQuestionsLeft()) {
            Question nextQuestion = quiz.takeNextQuestion();
            String title = String.format(getString(R.string.question_number_of_total), quiz.currentQuestionNumber(), quiz.totalQuestionNumber());
//            title = title + " (" + nextQuestion.getWord().getLanguage().getName() + ")";
            getSupportActionBar().setTitle(title);
            display(nextQuestion);

        }
        else {
            goToResults();
        }

        answerButton.setOnClickListener(buttonAnswerClick);
    }

    public void onCorrectAnswer() {
        AlertUtils.showToastWithText(this, R.string.answer_correct, R.color.good);
        takeNextQuestionOrGoToResults();
    }

    public void goToResults() {
        quiz.store();
        QuizResultsActivity.open(this, quiz.getResults());
        finish();
    }

    public void onWrongAnswer() {
        AlertUtils.showToastWithText(this, R.string.answer_wrong, R.color.bad);
        takeNextQuestionOrGoToResults();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton(android.R.string.cancel, null).create();

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.quit_are_you_sure_title));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.test_lost_progress));

        // Showing Alert Message
        alertDialog.show();
    }
}
