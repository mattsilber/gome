package com.guardanis.gome.tools.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.guardanis.imageloader.ImageUtils;

public class ViewHelper {

    public static void setBackgroundResourceAndKeepPadding(View v, int resourceId) {
        int top = v.getPaddingTop();
        int left = v.getPaddingLeft();
        int right = v.getPaddingRight();
        int bottom = v.getPaddingBottom();

        v.setBackgroundResource(resourceId);
        v.setPadding(left, top, right, bottom);
    }

    @SuppressLint("NewApi")
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16)
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        else v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }

    public static void closeSoftInputKeyboard(EditText et) {
        et.clearFocus();
        closeSoftInputKeyboard((View) et);
    }

    public static void closeSoftInputKeyboard(View v) {
        ((InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }


    public static void openSoftInputKeyboard(EditText et) {
        ((InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInputFromWindow(et.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        et.performClick();
        et.requestFocus();
    }

    public static void openSoftInputKeyboardOnLayout(final EditText et) {
        et.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        removeOnGlobalLayoutListener(et, this);

                        et.postDelayed(() ->
                                openSoftInputKeyboard(et), 200);
                    }
                });

        et.requestLayout();
    }

    public static boolean isSoftKeyboardFinishedAction(TextView view, int action, KeyEvent event) {
        // Some devices return null event on editor actions for Enter Button
        return (action == EditorInfo.IME_ACTION_DONE || action == EditorInfo.IME_ACTION_GO || action == EditorInfo.IME_ACTION_SEND) && (event == null || event.getAction() == KeyEvent.ACTION_DOWN);
    }


    public static void addFinishedActionListener(final TextView textView, final Runnable onFinished) {
        textView.setOnEditorActionListener((view, action, keyEvent) -> {
            if (action == EditorInfo.IME_ACTION_NEXT || isSoftKeyboardFinishedAction(view, action, keyEvent)) {
                closeSoftInputKeyboard(textView);

                if (onFinished != null)
                    onFinished.run();

                return true;
            }

            return false;
        });
    }
}
