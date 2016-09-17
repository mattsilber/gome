package com.guardanis.gome.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.guardanis.fontutils.EditText;
import com.guardanis.gome.R;
import com.guardanis.gome.tools.adapter.MultiSelectAdapter;
import com.guardanis.gome.tools.adapter.SingleSelectAdapter;
import com.guardanis.gome.tools.views.ViewHelper;
import com.guardanis.imageloader.ImageRequest;
import com.guardanis.imageloader.views.SVGImageView;

import java.util.List;

public class DialogBuilder {

    protected Dialog dialog;
    protected ViewGroup view;

    protected DialogInterface.OnCancelListener cancelListener;

    public DialogBuilder(Context context) {
        view = (ViewGroup) View.inflate(context, R.layout.base__dialog, null);
        setPrimaryButton(R.string.alert_action_ok, (dialog1, which) -> dismissDialog());
    }

    public DialogBuilder setTitle(int messageResId){
        return setTitle(messageResId < 1
                ? ""
                : view.getResources().getString(messageResId));
    }

    public DialogBuilder setTitle(String message){
        ((TextView) view.findViewById(R.id.base__dialog_title))
                .setText(message);

        return this;
    }

    public DialogBuilder setMessage(int messageResId) {
        return setMessage(messageResId < 1
                ? ""
                : view.getResources().getString(messageResId));
    }

    public DialogBuilder setMessage(String message) {
        TextView content = (TextView) view.findViewById(R.id.base__dialog_message);
        content.setText(message);
        content.setVisibility(View.VISIBLE);
        return this;
    }

    public DialogBuilder setMessage(Spanned message) {
        TextView content = (TextView) view.findViewById(R.id.base__dialog_message);
        content.setText(message);
        content.setVisibility(View.VISIBLE);
        return this;
    }

    public DialogBuilder setTitleImage(String svgAsset, final DialogInterface.OnClickListener eventListener){
        SVGImageView image = (SVGImageView) view.findViewById(R.id.base__dialog_title_image);
        image.setVisibility(View.VISIBLE);
        image.setImageAsset(svgAsset);
        image.setOnClickListener(v -> {
            if(eventListener != null)
                eventListener.onClick(dialog, 1);

            dismissDialog();
        });

        return this;
    }

    public DialogBuilder setTitleImage(ImageRequest request, final DialogInterface.OnClickListener eventListener){
        SVGImageView image = (SVGImageView) view.findViewById(R.id.base__dialog_title_image);
        image.setVisibility(View.VISIBLE);
        image.setOnClickListener(v -> {
            if(eventListener != null)
                eventListener.onClick(dialog, 1);

            dismissDialog();
        });

        request.setTargetView(image)
                .execute();

        return this;
    }

    public DialogBuilder setSecondaryTitleImage(ImageRequest request, final DialogInterface.OnClickListener eventListener){
        SVGImageView image = (SVGImageView) view.findViewById(R.id.base__dialog_title_image2);
        image.setVisibility(View.VISIBLE);
        image.setOnClickListener(v -> {
            if(eventListener != null)
                eventListener.onClick(dialog, 1);

            dismissDialog();
        });

        request.setTargetView(image)
                .execute();

        return this;
    }

    public DialogBuilder setPrimaryButton(int buttonTextResId, final DialogInterface.OnClickListener eventListener) {
        return setPrimaryButton(view.getContext().getResources().getString(buttonTextResId < 1 ? R.string.alert_action_ok : buttonTextResId), eventListener);
    }

    public DialogBuilder setPrimaryButton(String buttonText, final DialogInterface.OnClickListener eventListener) {
        TextView buttonPrimary = (TextView) view.findViewById(R.id.base__dialog_action_positive);
        buttonPrimary.setText(buttonText);
        buttonPrimary.setOnClickListener(v -> {
            if(eventListener != null)
                eventListener.onClick(dialog, 1);

            dismissDialog();
        });

        return this;
    }

    public DialogBuilder hidePrimaryButton(){
        view.findViewById(R.id.base__dialog_action_positive)
                .setVisibility(View.GONE);

        return this;
    }

    public DialogBuilder setSecondaryButton(int buttonTextResId, final DialogInterface.OnClickListener eventListener) {
        return setSecondaryButton(view.getContext().getResources().getString(buttonTextResId < 1 ? R.string.alert_action_cancel : buttonTextResId), eventListener);
    }

    public DialogBuilder setSecondaryButton(String buttonText, final DialogInterface.OnClickListener eventListener) {
        TextView buttonSecondary = (TextView) view.findViewById(R.id.base__dialog_action_negative);
        buttonSecondary.setText(buttonText);
        buttonSecondary.setVisibility(View.VISIBLE);
        buttonSecondary.setOnClickListener(v -> {
            if(eventListener != null)
                eventListener.onClick(dialog, 0);

            dismissDialog();
        });

        return this;
    }

    public <T> DialogBuilder setSingleSelectItems(SingleSelectAdapter<T> adapter, @NonNull Callback<T> callback){
        ListView list = (ListView) LayoutInflater.from(view.getContext())
                .inflate(R.layout.base__dialog_list, view, false);

        adapter.setClickCallback(t -> {
            callback.onCalled(t);

            dismissDialog();
        });

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        hidePrimaryButton();

        return addContentView(list);
    }

    public <T> DialogBuilder setMultiSelectItems(MultiSelectAdapter<T> adapter, @NonNull Callback<List<T>> callback){
        ListView list = (ListView) LayoutInflater.from(view.getContext())
                .inflate(R.layout.base__dialog_list, view, false);

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setPrimaryButton(R.string.alert_action_ok, (d, v) ->
                callback.onCalled(adapter.getSelectedItems()));

        return addContentView(list);
    }

    public DialogBuilder setInputModeString(String initialText, Callback<String> callback){
        final EditText input = (EditText) LayoutInflater.from(view.getContext())
                .inflate(R.layout.base__dialog_input, view, false);

        input.setText(initialText);

        ViewHelper.addFinishedActionListener(input, () -> {
            ViewHelper.closeSoftInputKeyboard(input);
            dismissDialog();

            callback.onCalled(input.getText().toString());
        });

        setPrimaryButton(R.string.alert_action_ok, (d, v) -> {
            ViewHelper.closeSoftInputKeyboard(input);
            dismissDialog();

            callback.onCalled(input.getText().toString());
        });

        return addContentView(input);
    }

    public DialogBuilder hideSecondaryButton(){
        view.findViewById(R.id.base__dialog_action_negative)
                .setVisibility(View.GONE);

        return this;
    }

    public DialogBuilder addContentView(int resId){
        return addContentView(LayoutInflater.from(view.getContext())
                .inflate(resId, view, false));
    }

    public DialogBuilder addContentView(View content){
        ((LinearLayout) view.findViewById(R.id.base__dialog_content_parent))
                .addView(content);

        return this;
    }

    public DialogBuilder addScrollingContentView(View content){
        ((LinearLayout) view.findViewById(R.id.base__dialog_scrolling_content_parent))
                .addView(content);

        return this;
    }

    public DialogBuilder setCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    public View findViewById(int id){
        return view.findViewById(id);
    }

    public Dialog show() {
        dialog = new AlertDialog.Builder(view.getContext())
                .setView(view)
                .show();

        if(cancelListener == null)
            dialog.setCancelable(false);
        else dialog.setOnCancelListener(cancelListener);

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void dismissDialog() {
        safelyDismiss(dialog);
    }

    public static void safelyDismiss(Dialog dialog){
        try{
            // Attempt to prevent stupid fucking uncatchable system error "View not Attached to Window" when dialog dismissed
            Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();
            if(context instanceof Activity) {
                if(!((Activity) context).isFinishing()){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if(!((Activity) context).isDestroyed())
                            dialog.dismiss();
                    }
                    else dialog.dismiss();
                }
            }
            else dialog.dismiss();
        }
        catch(IllegalArgumentException e){ }
        catch(Throwable e){ }
    }

    public static Dialog displayConfirmation(Context context, String message, @NonNull Runnable onConfirm, @Nullable Runnable onCancel){
        return new DialogBuilder(context)
                .setTitle(R.string.alert_title_confirm)
                .setMessage(message)
                .setPrimaryButton(R.string.alert_action_confirm, (d, v) -> onConfirm.run())
                .setSecondaryButton(R.string.alert_action_cancel, (d, v) -> {
                    if(onCancel != null)
                        onCancel.run();
                })
                .show();
    }

    /**
     * Show a loading dialog that will not die until explicitly canceled
     */
    public static Dialog displayLoadingDialog(Context context, @Nullable Runnable cancel){
        return displayLoadingDialog(context,
                context.getString(R.string.alert_title_loading),
                context.getString(R.string.alert_message_please_wait),
                cancel);
    }

    /**
     * Show a loading dialog that will not die until explicitly canceled
     */
    public static Dialog displayLoadingDialog(Context context, String title, String message, @Nullable Runnable cancel){
        DialogBuilder builder = new DialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .hidePrimaryButton();

        if(cancel != null)
            builder.setCancelListener(c -> cancel.run());

        return builder.show();
    }

}
