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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity(R.layout.activity_quiz)
public class QuizActivity extends VocabularyActivity {
    @ViewById(R.id.editTextAnswer)
    protected EditText editTextAnswer;
    @ViewById(R.id.textViewQuestion)
    protected TextView textViewQuestion;
    @ViewById(R.id.buttonAnswer)
    protected Button answerButton;
    @ViewById(R.id.textViewTip)
    protected TextView textViewTip;

    private Quiz quiz;

    public static void open(Context context)
    {
        Intent intent = QuizActivity_.intent(context).get();
        context.startActivity(intent);
    }

    @AfterViews
    protected void init()
    {
        List<Word> words1 = getDictionary().getAllWords();
        this.quiz = new Quiz(getDictionary(), words1);

        takeNextQuestionOrGoToResults();
    }

    private void display(Question question)
    {
        Word word = question.getWord();
        textViewQuestion.setText(word.getSpelling());
        editTextAnswer.setText("");

        if (question.getMemory() == null)
        {
            textViewTip.setText(getString(R.string.answer_no_tip));
        }
        else {
            textViewTip.setText(String.format(getString(R.string.answer_tip), question.getMemory().getDescription()));
        }
    }

    @TextChange(R.id.editTextAnswer)
    protected void onTextChange()
    {
        answerButton.setEnabled(editTextAnswer.getText().length() > 0);
    }

    @Click(R.id.buttonAnswer)
    protected void onAnswerClicked()
    {
        boolean correct = quiz.answer(editTextAnswer.getText().toString());
        if (correct)
            onCorrectAnswer();
        else
            onWrongAnswer();
    }

    @Click(R.id.buttonTakeNext)
    protected void onTakeNextQuestion()
    {
        quiz.skipQuestion();
        takeNextQuestionOrGoToResults();
    }

    private void takeNextQuestionOrGoToResults()
    {
        if (quiz.hasQuestionsLeft())
            display(quiz.takeNextQuestion());
        else
            goToResults();
    }

    public void onCorrectAnswer()
    {
        Toast.makeText(this, R.string.answer_correct, Toast.LENGTH_LONG).show();
        takeNextQuestionOrGoToResults();
    }

    public void goToResults()
    {
        QuizResultsActivity.open(this, quiz.getResults());
        finish();
    }
    public void onWrongAnswer()
    {
        Toast.makeText(this, R.string.answer_wrong, Toast.LENGTH_LONG).show();
    }
}
