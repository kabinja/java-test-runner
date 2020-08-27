package tech.ikora.selenium.locator.evolution.process;

import java.util.*;

public class Entries implements Iterable<Entry> {
    private final List<Entry> entryList;

    public Entries(){
        entryList = new ArrayList<>();
    }

    public void put(String key, String value){
        entryList.add(new Entry(key, value));
    }

    public boolean isEmpty(){
        return entryList.isEmpty();
    }

    @Override
    public Iterator<Entry> iterator() {
        return entryList.iterator();
    }
}
