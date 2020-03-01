package com.guardanis.gome.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import com.guardanis.gome.tools.observables.DebouncedEventController;

public class PreventTextWatcher implements TextWatcher, Callback<String> {

    private TextView textView;
    private Callback<String> textAddedListener;

    private DebouncedEventController clearController = new DebouncedEventController(this, 750);

    protected PreventTextWatcher(TextView textView, Callback<String> textAddedListener) {
        this.textView = textView;
        this.textAddedListener = textAddedListener;

        clearController.trigger("");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        clearController.trigger(s == null ? "" : s.toString());
    }

    public static void attachTo(TextView textView, Callback<String> textAddedListener) {
        textView.addTextChangedListener(
                new PreventTextWatcher(textView, text -> textAddedListener.onCalled(text)));
    }

    @Override
    public void onCalled(String value) {
        if (value != null && 0 < value.length()) {
            textAddedListener.onCalled(value);

            textView.setText("");
        }
    }
}
