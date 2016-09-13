package com.guardanis.gtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtils<V> {

    public interface Filter<V> {
        public boolean isFilterMatched(V obj);
    }

    public interface Converter<V, T> {
        public T convert(V from);
    }

    public interface Zipper<V, T, U> {
        public U zip(V o1, T o2);
    }

    public interface Reducer<T, V>{
        public T reduce(T last, V value);
    }

    public interface FAction<V> {
        public void executeFAction(V value);
    }

    private List<V> values;

    public ListUtils(V[] values){
        this(Arrays.asList(values));
    }

    public ListUtils(List<V> values){
        this.values = values;
    }

    public <T> ListUtils<T> map(Converter<V, T> converter){
        List<T> returnables = new ArrayList<T>();

        for(V value : values)
            returnables.add(converter.convert(value));

        return new ListUtils(returnables);
    }

    public ListUtils<Object> flatMap(){
        List<Object> returnables = new ArrayList<Object>();

        for(Object o : values)
            if(o instanceof List)
                returnables.addAll(new ListUtils((List) o)
                    .flatMap()
                    .values());
            else if(o instanceof Object[])
                returnables.addAll(new ListUtils((Object[]) o)
                        .flatMap()
                        .values());
            else returnables.add(o);

        return new ListUtils(returnables);
    }

    public <C> ListUtils<V> filter(Filter<V> filter){
        List<V> returnables = new ArrayList<V>();

        for(V value : values)
            if(filter.isFilterMatched(value))
                returnables.add(value);

        return new ListUtils(returnables);
    }

    public <T> T reduce(T initial, Reducer<T, V> reducer){
        for(V value : values)
            initial = reducer.reduce(initial, value);

        return initial;
    }

    public ListUtils<V> each(FAction action){
        for(V value : values)
            action.executeFAction(value);

        return this;
    }

    public ListUtils<V> take(int count){
        return take(count, 0);
    }

    public ListUtils<V> take(int count, int offset){
        List<V> returnables = new ArrayList<V>();

        for(int i = offset; i < offset + count && i < values.size(); i++)
            returnables.add(values.get(i));

        return new ListUtils(returnables);
    }

    public <T, U> ListUtils<U> zipWith(List<T> subset, ListUtils.Zipper<V, T, U> zipper){
        if(values.size() != subset.size())
            throw new IllegalArgumentException("Subset size [" + subset.size() + "] does not match values [" + values.size() + "]");

        List<U> returnables = new ArrayList<U>();

        for(int i = 0; i < values.size(); i++)
            returnables.add(zipper.zip(values.get(i), subset.get(i)));

        return new ListUtils(returnables);
    }

    public String join(String delimiter){
        return join(delimiter, new Converter<V, String>() {
            public String convert(V from) {
                return from.toString();
            }
        });
    }

    public String join(String delimiter, Converter<V, String> converter){
        String joined = "";

        if(0 < values.size()){
            joined += converter.convert(values.get(0));

            for(int i = 1; i < values.size(); i++)
                joined += delimiter + converter.convert(values.get(i));
        }

        return joined;
    }

    public List<V> values(){
        return values;
    }

    public static <T> ListUtils<T> from(T[] values){
        return new ListUtils<T>(values);
    }

    public static <T> ListUtils<T> from(List<T> values){
        return new ListUtils<T>(values);
    }

}
