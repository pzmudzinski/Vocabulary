package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.ListView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.Quiz;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.screens.lists.TopWordsActivity_;
import com.pz.vocabulary.app.screens.lists.WordsTwoTabsListActivity;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.sql.QuizHistory;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.views.ScoreView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotr on 27.04.2014.
 */
@EFragment(R.layout.fragment_stats)
public class StatisticsFragment extends VocabularyFragment implements Updatable, Arguments {

    @ViewById(R.id.listView)
    protected ListView listView;

    private DataProcessor dataProcessor;

    @AfterViews
    protected void init()
    {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && this.dataProcessor != null)
            refresh();
    }

    public void refresh()
    {
        loadAndShowStats();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Click(R.id.buttonShowAllWords)
    protected void showAllWords()
    {
        List<Language> languages = getDictionary().getLanguages();
        String[] titles = new String[] {
                languages.get(0).getName().toUpperCase(),
                languages.get(1).getName().toUpperCase()
        };

        WordsTwoTabsListActivity.open(getActivity(), titles, new String[]{
                SHOW_WORDS_FROM_LANGUAGE,
                SHOW_WORDS_FROM_LANGUAGE
        }, new long[] {
                languages.get(0).getId(),
                languages.get(1).getId()
        }); ;
    }

    @Click(R.id.buttonShowRankings)
    protected void showRankings()
    {
        TopWordsActivity_.intent(getActivity()).start();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser )
        {
            refresh();
        } else {
            if (dataProcessor != null)
            {
                dataProcessor.cancel(true);
            }
        }
    }

    protected void loadAndShowStats()
    {
        this.dataProcessor = new DataProcessor(getActivity(), getResources());
        dataProcessor.execute();
    }

    @UiThread
    protected void showStats(List<Pair<String,Object>> stats)
    {
        this.dataProcessor = null;
        if (this.listView.getAdapter() == null)
        {
            KeyValueAdapter adapter = new KeyValueAdapter(getActivity(), stats);
            this.listView.setAdapter(adapter);
        } else {
            KeyValueAdapter adapter = (KeyValueAdapter) listView.getAdapter();
            adapter.refill(stats);
        }
    }

    @Override
    public void update() {
        refresh();
    }

    public class DataProcessor extends AsyncTask<Void, Void, List<Pair<String,Object>>> {

        private Context mContext;
        private Resources resources;

        public DataProcessor(Context context, Resources resources){
            this.mContext = context;
            this.resources = resources;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Pair<String,Object>> doInBackground(Void... params) {
            PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder().
                    minimumPrintedDigits(2).
                    appendHours()
                    .printZeroAlways().
                            minimumPrintedDigits(2)
                    .appendMinutes()
                    .appendSeparator(":").
                            printZeroAlways().
                            minimumPrintedDigits(2)
                    .appendSeconds()
                    .toFormatter();

            List<Pair<String,Object>> stats = new ArrayList<Pair<String, Object>>();
            Dictionary dictionary = getDictionary();
            stats.add(Pair.create(
                            resources.getString(R.string.stats_translation_number),
                            (Object)dictionary.numberOfItems(Word.class))
            );
            stats.add(Pair.create(
                            resources.getString(R.string.stats_responses_all),
                            (Object)dictionary.numberOfAllResponses())
            );
            stats.add(Pair.create(
                            resources.getString(R.string.stats_responses_correct),
                            (Object)dictionary.numberOfResponsesWithResult(QuizHistory.QuizQuestionResult.ResponseCorrect))
            );
            stats.add(Pair.create(
                            resources.getString(R.string.stats_responses_wrong),
                            (Object)dictionary.numberOfResponsesWithResult(QuizHistory.QuizQuestionResult.ResponseWrong))
            );

            stats.add(Pair.create(
                    resources.getString(R.string.stats_responses_skipped),
                    (Object)dictionary.numberOfResponsesWithResult(QuizHistory.QuizQuestionResult.ResponseSkipped)
            ));

            stats.add(Pair.create(
                    resources.getString(R.string.stats_all_tests),
                    (Object)dictionary.numberOfItems(Quiz.class)
            ));

            stats.add(Pair.create(
                    resources.getString(R.string.stats_tests_duration),
                    (Object)dictionary.quizTotalTimeSpent().toString(minutesAndSeconds)
            ));

            stats.add(Pair.create(
                    resources.getString(R.string.stats_tests_average_duration),
                    (Object)dictionary.quizAverageTimeSpent().toString(minutesAndSeconds)
            ));

            String score = ScoreView.percentText(dictionary.quizAverageScore());
            stats.add(Pair.create(
                    resources.getString(R.string.stats_tests_average_result),
                    (Object)score
            ));
            // Your code here
            return stats;
        }

        @Override
        protected void onPostExecute(List<Pair<String,Object>> stats) {
            showStats(stats);
        }
    }

}
