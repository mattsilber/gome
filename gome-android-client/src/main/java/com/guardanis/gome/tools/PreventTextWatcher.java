package com.guardanis.gome.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class PreventTextWatcher implements TextWatcher {

    private Callback<String> textAddedListener;

    protected PreventTextWatcher(Callback<String> textAddedListener){
        this.textAddedListener = textAddedListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s != null && 0 < s.length())
            textAddedListener.onCalled(s.toString());
    }

    public static void attachTo(TextView textView, Callback<String> textAddedListener){
        textView.addTextChangedListener(new PreventTextWatcher(text -> {
            textAddedListener.onCalled(text);
            textView.setText("");
        }));
    }

}
