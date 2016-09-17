package com.guardanis.gome.tools.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guardanis.fontutils.TextView;
import com.guardanis.gome.R;
import com.guardanis.gome.tools.Callback;

import java.util.List;

public abstract class SingleSelectAdapter<T> extends FilterableArrayAdapter<T> {

    protected Callback<T> selectCallback;
    protected Callback<T> longClickCallback;

    public SingleSelectAdapter(Context context, @NonNull List data) {
        super(context, R.layout.base__single_select_item, data);
    }

    public SingleSelectAdapter<T> setClickCallback(Callback<T> selectCallback){
        this.selectCallback = selectCallback;
        return this;
    }

    public SingleSelectAdapter<T> setLongClickCallback(Callback<T> longClickCallback){
        this.longClickCallback = longClickCallback;
        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.base__single_select_item, parent, false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        final T item = getItem(position);

        holder.value.setText(getValue(item));

        String subText = getSubValue(item);
        if(subText.length() < 1)
            holder.sub.setVisibility(View.GONE);
        else{
            holder.sub.setVisibility(View.VISIBLE);
            holder.sub.setText(subText);
        }

        if(selectCallback != null)
            convertView.setOnClickListener(
                v -> selectCallback.onCalled(item));

        if(longClickCallback != null)
            convertView.setOnLongClickListener(v -> {
                longClickCallback.onCalled(item);

                return true;
            });

        return convertView;
    }

    protected abstract String getValue(T item);
    protected abstract String getSubValue(T item);

    protected static class ViewHolder {
        TextView value;
        TextView sub;

        ViewHolder(View convertView){
            this.value = (TextView) convertView.findViewById(R.id.base__dialog_selection_list_item_value);
            this.sub = (TextView) convertView.findViewById(R.id.base__dialog_selection_list_item_value_sub);
        }

    }

}
