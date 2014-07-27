package com.pz.vocabulary.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.utils.ColorUtils;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by piotr on 07/07/14.
 */
@EViewGroup(R.layout.view_score)
public class ScoreView extends RelativeLayout {

    @ViewById(R.id.textView)
    protected TextView textView;

    public ScoreView(Context context) {
        super(context);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void display(float score)
    {
        String scoreString = Integer.toString(roundUp(Math.round(score*100)));
        textView.setText(scoreString+"%");
        textView.setBackgroundColor(ColorUtils.getColor(1.0f - score));
    }

    int roundUp(int n) {
        return (n + 4) / 5 * 5;
    }
}
