package com.pz.vocabulary.app.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.pz.vocabulary.app.R;

/**
 * Created by piotr on 03/07/14.
 */
public class AlertUtils {

    //toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);

    public static void showToastWithText(Context context, int stringID, int bcgColor)
    {
        Toast toast = Toast.makeText(context, stringID, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.getView().setBackgroundColor(context.getResources().getColor(bcgColor));
        toast.show();
    }

    public static void showToastWithText(Context context, int stringID)
    {
        showToastWithText(context, stringID, R.color.neutral);
    }

    public static void showLackOfWordsToast(Context context)
    {
        showToastWithText(context, R.string.empty_words_database);
    }

    public static void showEmptyTestToast(Context context)
    {
        showToastWithText(context, R.string.test_empty);
    }
}
