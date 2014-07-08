package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pz.vocabulary.app.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by piotr on 05/07/14.
 */
@EViewGroup(R.layout.row_key_value)
public class NameValueRow extends RelativeLayout {
    @ViewById(R.id.name)
    public TextView nameTextView;

    @ViewById(R.id.value)
    public TextView valueTextView;

    public NameValueRow(Context context) {
        super(context);
    }

    public NameValueRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NameValueRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void displayLong(Pair<String,Long> pair)
    {
        nameTextView.setText(pair.first + ":");
        valueTextView.setText(String.valueOf(pair.second));
    }

    public void displayString(Pair<String, String> pair)
    {
        nameTextView.setText(pair.first + ":");
        valueTextView.setText(pair.second);
    }

    public void displayFloat(Pair<String, Float> pair)
    {
        nameTextView.setText(pair.first +":");
        valueTextView.setText(Float.toString(pair.second));
    }

    public void display(Pair<String, Object> pair)
    {
        if (pair.second instanceof Long)
            displayLong(Pair.create(pair.first, (Long)pair.second));
        else if (pair.second instanceof String)
            displayString(Pair.create(pair.first, (String)pair.second));
        else if (pair.second instanceof Float)
            displayFloat(Pair.create(pair.first, (Float)pair.second));
    }
}
