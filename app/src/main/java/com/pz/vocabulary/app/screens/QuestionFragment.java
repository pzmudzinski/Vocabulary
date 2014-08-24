package com.pz.vocabulary.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.QuizHistory;
import com.pz.vocabulary.app.utils.AlertUtils;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.utils.DictionaryUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by piotr on 19/08/14.
 */
@EFragment(R.layout.fragment_question)
public class QuestionFragment extends VocabularyFragment implements Arguments{

    @ViewById(R.id.editTextAnswer)
    protected EditText editTextAnswer;
    @ViewById(R.id.textViewQuestion)
    protected TextView textViewQuestion;
    @ViewById(R.id.buttonAnswer)
    protected Button answerButton;
    @ViewById(R.id.buttonTakeNext)
    protected Button skipButton;
    @ViewById(R.id.textViewTip)
    protected TextView textViewTip;
    @ViewById(R.id.spaceView)
    protected View spaceView;
    @ViewById(R.id.postAnswerView)
    protected ViewGroup postAnswerView;
    @ViewById(R.id.listView)
    protected ListView meaningsListView;

    public static final String ARG_QUESTION_NUMBER = "arg_question_number";

    private QuestionFragmentCallback callback;
    private int questionNumber;
    private Question question;
    private Handler handler = new Handler();

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

        QuizResponse response = callback.getQuiz().getResponseFor(questionNumber);
        if (response != null)
        {
            showState(response);
        }
    }

    private void refresh()
    {
        display(question);
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
        showStateDelayed();
    }

    public void onCorrectAnswer() {
        AlertUtils.showToastWithText(getActivity(), R.string.answer_correct, R.color.good);
        callback.onCorrectAnswer(questionNumber);
        showStateDelayed();
    }

    public void onWrongAnswer() {
        AlertUtils.showToastWithText(getActivity(), R.string.answer_wrong, R.color.bad);
        callback.onWrongAnswer(questionNumber);
        showStateDelayed();
    }

    private void showStateDelayed()
    {
        final QuizResponse response = callback.getQuiz().getResponseFor(questionNumber);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showState(response);
            }
        }, QuizActivity.ANIMATION_DURATION + 1);
    }

    @AfterTextChange(R.id.editTextAnswer)
    protected void onTextChanged()
    {
        answerButton.setEnabled(editTextAnswer.getText().length() > 0);
    }

    public void showState(QuizResponse result)
    {
        if (!isAdded())
            return;

        boolean isCorrectAnswer = result.getResult() == QuizHistory.QuizQuestionResult.ResponseCorrect;

        editTextAnswer.setEnabled(false);
        answerButton.setVisibility(View.GONE);
        skipButton.setVisibility(View.GONE);

        editTextAnswer.setText(result.getResponse());

        if (!isCorrectAnswer)
        {
            spaceView.setVisibility(View.GONE);
            postAnswerView.setVisibility(View.VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putLong(SHOW_WORD_MEANINGS, question.getWord().getId());
            List<Word> words  = DictionaryUtils.getWordsFromBundle(getDictionary(), bundle);

            ArrayAdapter<Word> adapter = new ArrayAdapter<Word>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    words);

            meaningsListView.setAdapter(adapter);
        } else {
            spaceView.setVisibility(View.VISIBLE);
            postAnswerView.setVisibility(View.GONE);
        }
    }


    @Click(R.id.acceptAnswerButton)
    protected void acceptAnswer()
    {
        showState( callback.getQuiz().acceptAnswer(questionNumber));
    }

    @Click(R.id.acceptAnswerAddMeaningButton)
    protected void acceptAnswerAndAddMeaning()
    {
        showState(callback.getQuiz().acceptAnswerAndAddMeaning(questionNumber));
    }
}
