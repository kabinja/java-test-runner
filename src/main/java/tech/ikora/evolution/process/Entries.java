package tech.ikora.evolution.process;

import tech.ikora.evolution.configuration.Entry;

import java.util.*;

public class Entries implements Iterable<Entry> {
    private final List<Entry> entryList;

    public Entries(){
        entryList = new ArrayList<>();
    }

    public Entries(int size){
        entryList = new ArrayList<>(size);
    }

    public void put(String key, String value){
        entryList.add(new Entry(key, value));
    }

    public void putAll(List<Entry> extraArguments) {
        entryList.addAll(extraArguments);
    }

    public boolean isEmpty(){
        return entryList.isEmpty();
    }

    @Override
    public Iterator<Entry> iterator() {
        return entryList.iterator();
    }
}
