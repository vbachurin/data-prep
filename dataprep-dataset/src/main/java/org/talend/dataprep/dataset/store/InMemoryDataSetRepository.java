package org.talend.dataprep.dataset.store;

import java.util.HashMap;
import java.util.Map;

class InMemoryDataSetRepository implements DataSetRepository {

    private final Map<String, DataSet> store = new HashMap<>();

    @Override
    public Iterable<DataSet> list() {
        return store.values();
    }

    @Override
    public void add(DataSet dataSet) {
        store.put(dataSet.getId(), dataSet);
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
    public DataSet get(String id) {
        return store.get(id);
    }

    @Override
    public void remove(String id) {
        store.remove(id);
    }
}
