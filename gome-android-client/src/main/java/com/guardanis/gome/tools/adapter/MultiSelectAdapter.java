package com.guardanis.gome.tools.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.guardanis.fontutils.TextView;
import com.guardanis.gome.R;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiSelectAdapter<T> extends FilterableArrayAdapter<T> {

    protected List<T> selectedItems = new ArrayList<T>();

    public MultiSelectAdapter(Context context, @NonNull List data) {
        super(context, R.layout.base__multi_select_item, data);
    }

    public MultiSelectAdapter setSelectedItems(List<T> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();

        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.base__multi_select_item, parent, false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        final T item = getItem(position);

        holder.value.setText(getValue(item));

        String subText = getSubValue(item);
        if (subText.length() < 1)
            holder.sub.setVisibility(View.GONE);
        else {
            holder.sub.setVisibility(View.VISIBLE);
            holder.sub.setText(subText);
        }

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedItems.contains(item));
        holder.checkBox.setOnCheckedChangeListener((v, checked) -> {
            if (checked)
                selectedItems.add(item);
            else selectedItems.remove(item);

            notifyDataSetChanged();
        });

        return convertView;
    }

    protected abstract String getValue(T item);

    protected abstract String getSubValue(T item);

    protected static class ViewHolder {
        AppCompatCheckBox checkBox;
        TextView value;
        TextView sub;

        ViewHolder(View convertView) {
            this.checkBox = (AppCompatCheckBox) convertView.findViewById(R.id.base__dialog_selection_list_item_checkbox);
            this.value = (TextView) convertView.findViewById(R.id.base__dialog_selection_list_item_value);
            this.sub = (TextView) convertView.findViewById(R.id.base__dialog_selection_list_item_value_sub);
        }

    }

    public List<T> getSelectedItems() {
        return selectedItems;
    }

}
