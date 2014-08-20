package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.export.ExportActivity_;
import com.pz.vocabulary.app.export.ImportActivity_;
import com.pz.vocabulary.app.models.Question;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.utils.AlertUtils;
import com.pz.vocabulary.app.utils.Arguments;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by piotr on 06/06/14.
 */
@EActivity
public class QuizActivity extends VocabularyActionBarActivity implements IntentArguments, QuestionFragment.QuestionFragmentCallback {

    private Quiz quiz;

    MyAdapter mAdapter;

    ViewPager mPager;

    public static void open(Context context, List<Word> words)
    {
        Intent intent = QuizActivity_.intent(context).get();
        Word[] wordsAsArray = words.toArray(new Word[words.size()]);
        intent.putExtra(ARG_WORD_IDS, wordsAsArray);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        Parcelable[] parcelables = getIntent().getParcelableArrayExtra(ARG_WORD_IDS);
        Word[] words = Arrays.copyOf(parcelables, parcelables.length, Word[].class);;

        getSupportActionBar().setTitle("");

        this.quiz = new Quiz(getDictionary(), Arrays.asList(words));

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        setActionBarTitleForQuestion(0);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setActionBarTitleForQuestion(position);
            }
        });
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new DecelerateInterpolator();
            FixedSpeedScroller scroller = new FixedSpeedScroller(mPager.getContext(), sInterpolator);
            // scroller.setFixedDuration(5000);
            mScroller.set(mPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private synchronized void takeNextQuestionOrGoToResults(int questionNumber) {
        if (questionNumber < quiz.totalQuestionNumber() - 1) {
            setActionBarTitleForQuestion(questionNumber);
            mAdapter.increaseStage();
            mPager.setCurrentItem(mAdapter.getStage());
        }
        else {
            goToResults();
        }
    }

    private void setActionBarTitleForQuestion(int questionNumber)
    {
        String title = String.format(getString(R.string.question_number_of_total), questionNumber+1, quiz.totalQuestionNumber());
        getSupportActionBar().setTitle(title);
    }

    public void goToResults() {
        quiz.store();
        QuizResultsActivity.open(this, quiz.getResults());
        finish();
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
        alertDialog.setTitle(getString(R.string.quit_are_you_sure_title));
        alertDialog.setMessage(getString(R.string.test_lost_progress));
        alertDialog.show();
    }

    @Override
    public Quiz getQuiz() {
        return quiz;
    }

    @Override
    public void onCorrectAnswer(int questionNumber) {
        takeNextQuestionOrGoToResults(questionNumber);
    }

    @Override
    public void onWrongAnswer(int questionNumber) {
        takeNextQuestionOrGoToResults(questionNumber);
    }

    @Override
    public void onSkipQuestion(int questionNumber) {
        takeNextQuestionOrGoToResults(questionNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_finish_test) {
            goToResults();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        private int stage = 1;
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return stage;
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(position);
        }

        public void increaseStage()
        {
            stage++;
            notifyDataSetChanged();
        }

        public int getStage()
        {
            return stage;
        }


    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 600;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}
