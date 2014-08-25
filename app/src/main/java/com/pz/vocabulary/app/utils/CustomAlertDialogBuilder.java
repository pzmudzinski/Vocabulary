package com.pz.vocabulary.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;

import com.pz.vocabulary.app.R;

/**
 * Created by piotr on 24/08/14.
 */
public class CustomAlertDialogBuilder extends AlertDialog.Builder {
    public CustomAlertDialogBuilder(Context context) {
        super( context);
    }

    public CustomAlertDialogBuilder(Context context, int theme) {
        super(context, theme);
    }
}
