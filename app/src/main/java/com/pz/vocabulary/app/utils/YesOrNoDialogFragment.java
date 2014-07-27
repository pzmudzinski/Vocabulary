package com.pz.vocabulary.app.utils;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pz.vocabulary.app.R;

/**
 * Created by piotr on 12/07/14.
 */
public class YesOrNoDialogFragment extends DialogFragment {


    public YesOrNoDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container);
        getDialog().setTitle("Hello");

        return view;
    }
}
