package com.guardanis.gome.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import com.guardanis.gome.tools.observables.DebouncedEventController;

public class PreventTextWatcher implements TextWatcher, Callback<String> {

    private TextView textView;
    private Callback<String> textAddedListener;

    private DebouncedEventController clearController = new DebouncedEventController(this, 750);

    private String lastSentText = "";

    protected PreventTextWatcher(TextView textView, Callback<String> textAddedListener) {
        this.textView = textView;
        this.textAddedListener = textAddedListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (s != null) {
            String text = s.toString();

            if (lastSentText.length() < text.length()) {
                String updated = text.substring(lastSentText.length());

                textAddedListener.onCalled(updated);
            }

            lastSentText = text;
        }

        clearController.trigger("");
    }

    public static void attachTo(TextView textView, Callback<String> textAddedListener) {
        textView.addTextChangedListener(
                new PreventTextWatcher(textView, text -> textAddedListener.onCalled(text)));
    }

    @Override
    public void onCalled(String value) {
        textView.setText("");
    }
}
