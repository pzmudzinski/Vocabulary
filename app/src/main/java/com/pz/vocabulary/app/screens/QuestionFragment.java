package com.pz.vocabulary.app.screens;

import android.app.Activity;
import android.os.Bundle;
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
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by piotr on 19/08/14.
 */
@EFragment(R.layout.fragment_question)
public class QuestionFragment extends VocabularyFragment{

    @ViewById(R.id.editTextAnswer)
    protected EditText editTextAnswer;
    @ViewById(R.id.textViewQuestion)
    protected TextView textViewQuestion;
    @ViewById(R.id.buttonAnswer)
    protected Button answerButton;
    @ViewById(R.id.textViewTip)
    protected TextView textViewTip;
    public static final String ARG_QUESTION_NUMBER = "arg_question_number";

    private QuestionFragmentCallback callback;
    private int questionNumber;
    private Question question;

    public interface QuestionFragmentCallback
    {
        public Quiz getQuiz();
        public void onCorrectAnswer(int questionNumber);
        public void onWrongAnswer(int questionNumber);
        public void onSkipQuestion(int questionNumber);
    }

    public static QuestionFragment newInstance(int questionNumber)
    {
        QuestionFragment fragment = QuestionFragment_.builder().build();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_QUESTION_NUMBER, questionNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.questionNumber = getArguments().getInt(ARG_QUESTION_NUMBER);
        this.question = callback.getQuiz().takeQuestion(questionNumber);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.callback = (QuestionFragmentCallback) activity;


        } catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString() + "must implement QuestionFragmentCallback");
        }

    }

    @AfterViews
    protected void takeQuestionFromQuiz()
    {
        display(question);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callback = null;
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

    @Click(R.id.buttonAnswer)
    protected void onAnswerClicked() {
        Quiz quiz = callback.getQuiz();
        boolean correct = quiz.answer(questionNumber, editTextAnswer.getText().toString());
        if (correct)
            onCorrectAnswer();
        else
            onWrongAnswer();
    }

    @Click(R.id.buttonTakeNext)
    protected void onTakeNextQuestion() {
        callback.getQuiz().skipQuestion(questionNumber);
        callback.onSkipQuestion(questionNumber);
    }

    public void onCorrectAnswer() {
        AlertUtils.showToastWithText(getActivity(), R.string.answer_correct, R.color.good);
        callback.onCorrectAnswer(questionNumber);
    }

    public void onWrongAnswer() {
        AlertUtils.showToastWithText(getActivity(), R.string.answer_wrong, R.color.bad);
        callback.onWrongAnswer(questionNumber);
    }

    @AfterTextChange(R.id.editTextAnswer)
    protected void onTextChanged()
    {
        answerButton.setEnabled(editTextAnswer.getText().length() > 0);
    }

}
