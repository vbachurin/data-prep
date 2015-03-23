package org.talend.dataprep.preparation;

import java.util.HashMap;
import java.util.Map;

public class Repository {

    private final Map<String, Object> store = new HashMap<>();

    public Repository() {
        add(RootBlob.INSTANCE);
        add(RootStep.INSTANCE);
    }

    public void add(Object object) {
        store.put(object.id(), object);
    }

    public <T extends Object> T get(String id, Class<T> clazz) {
        if (id == null) {
            return null;
        }
        Object value = store.get(id);
        if (value == null) {
            return null;
        }
        if (clazz == null) {
            return (T) value;
        } else if (clazz.isAssignableFrom(value.getClass())) {
            return clazz.cast(value);
        } else {
            return null;
        }
    }
}
