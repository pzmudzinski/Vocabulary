package com.pz.vocabulary.app.screens;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Word;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

/**
 * Created by piotr on 27.04.2014.
 */
@EFragment(R.layout.fragment_add_translation)
public class AddTranslationFragment extends VocabularyFragment {
    @ViewById(R.id.editTextFrom)
    protected EditText from;
    @ViewById(R.id.editTextTo)
    protected EditText to;
    @ViewById(R.id.editTextMemory)
    protected EditText memory;
    @ViewById(R.id.buttonAddTranslation)
    protected Button addTranslationButton;

    @Click(R.id.buttonAddTranslation)
    public void onAddTranslation()
    {
        String textFrom = from.getText().toString();
        String textTo = to.getText().toString();

        String memoryText;
        Memory memory1 = null;
        if (!TextUtils.isEmpty(memory.getText()))
        {
            memoryText = memory.getText().toString();
            memory1 = new Memory(memoryText);
        }

        Dictionary dictionary = getDictionary();
        Language polish = dictionary.findLanguage(Language.POLISH);
        Language english = dictionary.findLanguage(Language.ENGLISH);

        Word polishWord = polish.newWord(textFrom);
        Word englishWord = english.newWord(textTo);
        dictionary.insertWordsAndTranslation(polishWord, englishWord, memory1);
        clearFocus();
    }

    private void clearFocus()
    {
        EditText[] editTexts = new EditText[] { from, to, memory};
        for (EditText editText : editTexts)
        {
            editText.setText("");
            editText.clearFocus();
        }
    }

    @TextChange({R.id.editTextFrom, R.id.editTextTo})
    protected void onTextChanged()
    {
        addTranslationButton.setEnabled(hasFilledSpellings());
    }

    private boolean hasFilledSpellings()
    {
        return from.getText().length() > 0 && to.getText().length() > 0;
    }
}
