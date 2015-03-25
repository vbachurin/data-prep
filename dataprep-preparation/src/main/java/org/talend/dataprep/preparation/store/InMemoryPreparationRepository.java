package org.talend.dataprep.preparation.store;

import org.talend.dataprep.preparation.Identifiable;
import org.talend.dataprep.preparation.PreparationRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.talend.dataprep.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.preparation.Step.ROOT_STEP;

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
    public <T extends Identifiable> Set<T> listAll(Class<T> clazz) {
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
