package com.pz.vocabulary.app.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.utils.AlertUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

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
    @ViewById(R.id.selectMemory)
    protected Button selectMemoryButton;

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
        List<Language> languages = dictionary.getLanguages();

        Language polish = languages.get(0);
        Language english = languages.get(1);

        Word polishWord = polish.newWord(textFrom);
        Word englishWord = english.newWord(textTo);
        dictionary.insertWordsAndTranslation(polishWord, englishWord, memory1);
        AlertUtils.showToastWithText(this.getActivity(), R.string.translation_added);
        clearFocus();
        setSelectMemoryButtonState();

    }

    @AfterViews
    public void setSelectMemoryButtonState()
    {
        boolean hasMemories = getDictionary().hasItems(Memory.class);
        selectMemoryButton.setVisibility(hasMemories? View.VISIBLE : View.GONE);
    }

    @Click(R.id.selectMemory)
    protected void selectMemoryClicked()
    {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(R.string.select_memory);
        List<Memory> memories = getDictionary().getAllMemories();
        final List<String> titles = new ArrayList<String>(memories.size());
        for (Memory memory : memories)
            titles.add(memory.getDescription());

         b.setItems(titles.toArray(new String[titles.size()]), new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 memory.setText(titles.get(i));
             }
         });

        b.show();
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
