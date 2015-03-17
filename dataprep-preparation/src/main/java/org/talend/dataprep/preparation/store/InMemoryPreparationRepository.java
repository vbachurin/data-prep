package org.talend.dataprep.preparation.store;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.preparation.Preparation;

public class InMemoryPreparationRepository implements PreparationRepository {

    private final Map<String, Preparation> store = new HashMap<>();

    @Override
    public Iterable<Preparation> list() {
        return store.values();
    }

    @Override
    public synchronized void add(Preparation dataSetMetadata) {
        store.put(dataSetMetadata.getId(), dataSetMetadata);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public Preparation get(String id) {
        return store.get(id);
    }

    @Override
    public void remove(String id) {
        store.remove(id);
    }
}
