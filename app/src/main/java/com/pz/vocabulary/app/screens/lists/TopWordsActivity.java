package com.pz.vocabulary.app.screens.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.utils.ColorUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotr on 15/07/14.
 */
@EActivity(R.layout.activity_top_words)
public class TopWordsActivity extends VocabularyListActivity {

    private static final int WORDS_PER_RANKINGS = 10;

    @AfterViews
    protected void init()
    {
        Dictionary dictionary = getDictionary();
        List<Word> top = dictionary.getTopScoredWords(WORDS_PER_RANKINGS);
        List <Word> worst = dictionary.getLeastScoredWords(WORDS_PER_RANKINGS);

        final List<Word> allWords = new ArrayList<Word>(top.size() + worst.size());
        int topWordsCount = top.size();
        int worstWordsCount = worst.size();
        allWords.addAll(top);
        allWords.addAll(worst);

        getListView().setAdapter(new TopWordsAdapter(this, topWordsCount, worstWordsCount, allWords));
    }

    private class TopWordsAdapter extends BaseAdapter
    {
        private int topWordsCount;
        private int worstWordsCount;
        private List<Word> allWords;
        private int topWordsSectionPos;
        private int worstWordsSectionPos;
        private Context context;
        public TopWordsAdapter(Context context, int topWordsCount, int worstWordsCount, List<Word> words)
        {
            super();
            this.topWordsCount = topWordsCount;
            this.worstWordsCount = worstWordsCount;
            this.allWords = words;
            this.context = context;
            this.topWordsSectionPos = 0;
            this.worstWordsSectionPos = topWordsCount + 1;
            this.allWords.add(topWordsSectionPos, null);
            this.allWords.add(worstWordsSectionPos, null);
        }

        @Override
        public int getCount() {
            return topWordsCount + worstWordsCount + 2;
        }

        @Override
        public Object getItem(int i) {
            return allWords.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == topWordsSectionPos || position == worstWordsSectionPos)
                return 0;
            else
                return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view;
            boolean isSection = (position == topWordsSectionPos || position == worstWordsSectionPos);
            if (convertView != null)
            {
                view = convertView;
            } else {
                if (isSection)
                {
                    view = LayoutInflater.from(context).inflate(R.layout.row_place_section, null);
                } else{
                    view = LayoutInflater.from(context).inflate(R.layout.row_place, null);
                }

            }

            if (isSection)
            {
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(position == topWordsSectionPos? context.getString(R.string.words_top) : context.getString(R.string.words_worst));
            } else{
                TextView placeTextView = (TextView) view.findViewById(R.id.placeTextView);
                TextView wordTextView = (TextView) view.findViewById(R.id.wordTextView);

                int place = (position % (topWordsCount + 1));
                placeTextView.setText(Integer.toString(place) + ". ");

                String percent = Float.toString(allWords.get(position).getScore()*100) + "%";
                wordTextView.setText(allWords.get(position).getSpelling() + " (" + percent + ")");
                view.setBackgroundColor(ColorUtils.getColor(1.0f - allWords.get(position).getScore()));
            }

            return view;
        }
    }
}
