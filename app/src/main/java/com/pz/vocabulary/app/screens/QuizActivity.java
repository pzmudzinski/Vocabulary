package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.AlertUtils;
import com.pz.vocabulary.app.utils.DictionaryUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

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

    @Extra(ARG_QUIZ_WORDS_ALL)
    public boolean showAllWords;
    @Extra(ARG_QUIZ_WORDS_SINCE)
    public int showWordsSince = -1;
    @Extra(ARG_QUIZ_WORDS_TOUGH)
    public boolean showToughWords;

    private Quiz quiz;

    public static void open(Context context) {
        Intent intent = QuizActivity_.intent(context).get();
        context.startActivity(intent);
    }

    @AfterViews
    protected void init() {
        List<Word> quizWords = null;

        getSupportActionBar().setTitle("");

        if (showAllWords) {
            quizWords = getDictionary().getAllWords();
        }
        else if (showWordsSince != -1) {

            quizWords = DictionaryUtils.getWordSince(getDictionary(), showWordsSince);
        }
        else if (showToughWords) {

        }


        this.quiz = new Quiz(getDictionary(), quizWords);
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
