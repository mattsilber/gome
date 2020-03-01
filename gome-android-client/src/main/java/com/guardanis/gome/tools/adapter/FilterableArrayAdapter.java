package com.guardanis.gome.tools.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.guardanis.collections.tools.ListUtils;
import com.guardanis.gome.BaseActivity;
import com.guardanis.gome.tools.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FilterableArrayAdapter<T> extends ArrayAdapter<T> {

    protected List<T> activeData;
    protected List<T> fullData = new ArrayList<T>();

    protected String lastFilter = "";

    private Map<String, Callback> callbacks = new HashMap<String, Callback>();

    public FilterableArrayAdapter(Context context, int resource, @NonNull List<T> data) {
        super(context, resource, data);
        this.activeData = data;

        for (T t : data)
            fullData.add(t);
    }

    @Override
    public void add(T item) {
        fullData.add(item);

        if (isFilterMatched(lastFilter, item))
            super.add(item);
    }

    public void filter(@NonNull String value) {
        lastFilter = value;

        this.activeData.clear();

        for (T t : new ListUtils<T>(fullData)
                .filter(data -> isFilterMatched(value, data))
                .values())
            activeData.add(t);

        notifyDataSetChanged();
    }

    protected abstract boolean isFilterMatched(@NonNull String value, T t);

    public void setFilterableDataSet(@NonNull List<T> values) {
        this.fullData = values;

        filter(lastFilter);
    }

    public List<T> getFullData() {
        return fullData;
    }

    public List<T> getActiveData() {
        return activeData;
    }

    public String getSearchFilter() {
        return lastFilter;
    }

    public FilterableArrayAdapter registerCallback(String key, Callback callback) {
        callbacks.put(key, callback);
        return this;
    }

    protected <V> void triggerCallback(String key, V value) {
        try {
            callbacks.get(key)
                    .onCalled(value);
        }
        catch (ClassCastException e) { e.printStackTrace(); }
        catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(BaseActivity.TAG__BASE, key + " is null. Ignoring.");
        }
    }
}
