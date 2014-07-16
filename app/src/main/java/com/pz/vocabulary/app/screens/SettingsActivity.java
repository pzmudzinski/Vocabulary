package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.ContextThemeWrapper;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.utils.Arguments;

import org.androidannotations.annotations.EActivity;

/**
 * Created by piotr on 06/07/14.
 */
@EActivity
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference button = (Preference)getPreferenceManager().findPreference("delete_all");
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    askToConfirmDelete();
                    return true;
                }
            });
        }

    }

    public void askToConfirmDelete()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(
           this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(Arguments.INTENT_RESULT_DELETE);
                finish();
            }
        }).setNegativeButton(android.R.string.cancel, null).create();

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.delete_all));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.delete_are_you_sure));

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.abc_ic_clear);

        // Showing Alert Message
        alertDialog.show();
    }
}
