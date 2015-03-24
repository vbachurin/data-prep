package org.talend.dataprep.api.preparation.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.talend.dataprep.api.preparation.Object;
import org.talend.dataprep.api.preparation.PreparationRepository;
import org.talend.dataprep.api.preparation.RootBlob;
import org.talend.dataprep.api.preparation.RootStep;

public class InMemoryPreparationRepository implements PreparationRepository {

    private final Map<String, Object> store = new HashMap<>();

    public InMemoryPreparationRepository() {
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

    @Override
    public <T extends Object> Set<T> listAll(Class<T> clazz) {
        return store.entrySet().stream().filter(entry -> clazz.isAssignableFrom(entry.getValue().getClass()))
                .map(entry -> (T) entry.getValue()).collect(Collectors.toSet());
    }

    @Override
    public void clear() {
        store.clear();
        add(RootBlob.INSTANCE);
        add(RootStep.INSTANCE);
    }
}
