package org.talend.dataprep.preparation.store;

import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.talend.dataprep.api.preparation.Identifiable;
import org.talend.dataprep.api.preparation.PreparationRepository;

public class InMemoryPreparationRepository implements PreparationRepository {

    private final Map<String, Identifiable> store = new HashMap<>();

    public InMemoryPreparationRepository() {
        add(ROOT_CONTENT);
        add(ROOT_STEP);
    }

    public void add(Identifiable object) {
        store.put(object.id(), object);
    }

    public <T extends Identifiable> T get(String id, Class<T> clazz) {
        if (id == null) {
            return null;
        }
        Identifiable value = store.get(id);
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
    public <T extends Identifiable> Collection<T> listAll(Class<T> clazz) {
        return store.entrySet().stream().filter(entry -> clazz.isAssignableFrom(entry.getValue().getClass()))
                .map(entry -> (T) entry.getValue()).collect(Collectors.toSet());
    }

    @Override
    public void clear() {
        store.clear();
        add(ROOT_CONTENT);
        add(ROOT_STEP);
    }
}
