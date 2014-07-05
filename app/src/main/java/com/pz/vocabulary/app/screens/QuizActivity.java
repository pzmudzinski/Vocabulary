package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.DictionaryUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.TextChange;
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


        takeNextQuestionOrGoToResults();
    }

    private void display(Question question) {
        Word word = question.getWord();
        textViewQuestion.setText(word.getSpelling());
        editTextAnswer.setText("");

        if (question.getMemory() == null) {
            textViewTip.setText(getString(R.string.answer_no_tip));
        } else {
            textViewTip.setText(String.format(getString(R.string.answer_tip), question.getMemory().getDescription()));
        }
    }

    @TextChange(R.id.editTextAnswer)
    protected void onTextChange() {
        answerButton.setEnabled(editTextAnswer.getText().length() > 0);
    }

    @Click(R.id.buttonAnswer)
    protected void onAnswerClicked() {
        boolean correct = quiz.answer(editTextAnswer.getText().toString());
        if (correct)
            onCorrectAnswer();
        else
            onWrongAnswer();
        takeNextQuestionOrGoToResults();
    }

    @Click(R.id.buttonTakeNext)
    protected void onTakeNextQuestion() {
        quiz.skipQuestion();
        takeNextQuestionOrGoToResults();
    }

    private void takeNextQuestionOrGoToResults() {
        if (quiz.hasQuestionsLeft()) {
            display(quiz.takeNextQuestion());
            getSupportActionBar().setTitle(String.format(getString(R.string.question_number_of_total), quiz.currentQuestionNumber(), quiz.totalQuestionNumber()));
        }
        else {
            goToResults();
        }
    }

    public void onCorrectAnswer() {
        Toast.makeText(this, R.string.answer_correct, Toast.LENGTH_SHORT).show();
        takeNextQuestionOrGoToResults();
    }

    public void goToResults() {
        QuizResultsActivity.open(this, quiz.getResults());
        finish();
    }

    public void onWrongAnswer() {
        Toast.makeText(this, R.string.answer_wrong, Toast.LENGTH_SHORT).show();
    }
}
