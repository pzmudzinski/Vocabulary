package com.pz.vocabulary.app.screens;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.pz.vocabulary.app.MainActivity_;
import com.pz.vocabulary.app.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by piotr on 09/08/14.
 */

@EActivity(R.layout.activity_select_language)
public class SelectLanguageActivity extends VocabularyActivity {

    @ViewById(R.id.foreignLanguageSpinner)
    protected Spinner foreignLangaugeSpinner;

    private Map<String, Locale> locales = new HashMap<String, Locale>();

    @AfterViews
    protected void init()
    {
        Locale[] locale = Locale.getAvailableLocales();

        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){

            country = loc.getDisplayLanguage();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
                locales.put(country, loc);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);


        ArrayAdapter<String> adapterForForeign = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);

        adapterForForeign.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        foreignLangaugeSpinner.setAdapter(adapterForForeign);

        foreignLangaugeSpinner.setSelection(countries.indexOf(Locale.ENGLISH.getDisplayLanguage()));
    }

    @Click(R.id.addButton)
    protected void addButtonClicked()
    {
        Locale usersLocale = getResources().getConfiguration().locale;


        String selectedLang = (String) foreignLangaugeSpinner.getAdapter().getItem(foreignLangaugeSpinner.getSelectedItemPosition());
        Locale selectedLocale = locales.get(selectedLang);

        getDictionary().addLanguages(usersLocale.getLanguage(), selectedLocale.getLanguage());
        MainActivity_.intent(this).start();
        finish();
    }

}
