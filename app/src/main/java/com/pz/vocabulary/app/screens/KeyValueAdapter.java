package com.pz.vocabulary.app.screens;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by piotr on 05/07/14.
 */
public class KeyValueAdapter extends BaseAdapter {

    private List<Pair<String,Object>> pairs;
    private Context context;

    public KeyValueAdapter(Context context, List<Pair<String,Object>> pairs)
    {
        super();
        this.pairs = pairs;
        this.context = context;
    }

    public void refill(List<Pair<String,Object>> pairs)
    {
        if (this.pairs != null)
        {
            this.pairs.clear();
            this.pairs.addAll(pairs);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pairs.size();
    }

    @Override
    public Object getItem(int i) {
        return pairs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        NameValueRow nameValueRow;
        if (convertView == null)
        {
            nameValueRow = NameValueRow_.build(context);
        }
        else {
            nameValueRow = (NameValueRow) convertView;
        }

        nameValueRow.display(pairs.get(i));
        return nameValueRow;
    }
}
