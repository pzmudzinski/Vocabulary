package com.pz.vocabulary.app.screens;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.pz.vocabulary.app.R;
import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Word;
import com.pz.vocabulary.app.sql.Dictionary;
import com.pz.vocabulary.app.utils.Arguments;
import com.pz.vocabulary.app.utils.DictionaryUtils;

/**
 * Created by piotr on 24/08/14.
 */
public class AddMeaningDialogFragment extends DialogFragment implements Arguments, TextWatcher, View.OnClickListener {

    private EditText translationEditText;
    private EditText memoryEditText;
    private Button addButton;

    private AddMeaningDialogListener listener;

    public interface AddMeaningDialogListener
    {
        public Dictionary getDictionary();
        public void onMeaningAdded();
    }

    public static AddMeaningDialogFragment newInstance(long wordID)
    {
        AddMeaningDialogFragment fragment = new AddMeaningDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_WORD_IDS, wordID);
        fragment.setArguments(bundle);
        return fragment;
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog=new Dialog(getActivity(),R.style.CustomDialog);
        dialog.setTitle(R.string.add_meaning);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_add_meaning, container);
        this.translationEditText = (EditText) v.findViewById(R.id.editText);
        this.memoryEditText = (EditText) v.findViewById(R.id.editText2);
        this.addButton = (Button) v.findViewById(R.id.button);

        translationEditText.addTextChangedListener(this);
        this.addButton.setOnClickListener(this);

        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (AddMeaningDialogListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        addButton.setEnabled(editable.length() > 0);
    }

    @Override
    public void onClick(View view) {
        Word word = listener.getDictionary().findWord(getArguments().getLong(ARG_WORD_IDS));
        Language language = DictionaryUtils.otherLanguage(listener.getDictionary(), word.getLanguageID());
        Word newWord = language.newWord(translationEditText.getText().toString());
        Memory memory = memoryEditText.getText().length() > 0? new Memory(memoryEditText.getText().toString()) : null;

        listener.getDictionary().insertWordsAndTranslation(word, newWord, memory);
        listener.onMeaningAdded();
        dismiss();
    }
}
