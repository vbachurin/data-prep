package org.talend.dataprep.preparation.store;

import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.preparation.Identifiable;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.PreparationRepository;

public class InMemoryPreparationRepository implements PreparationRepository {

    private final Map<String, Identifiable> store = new HashMap<>();

    public InMemoryPreparationRepository() {
        add(ROOT_CONTENT);
        add(ROOT_STEP);
    }

    @Override
    public void add(Identifiable object) {
        store.put(object.id(), object);
    }

    @Override
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

    /**
     * @see PreparationRepository#getByDataSet(String)
     */
    @Override
    public Collection<Preparation> getByDataSet(String dataSetId) {

        // defensive programming
        if (StringUtils.isEmpty(dataSetId)) {
            return Collections.emptyList();
        }

        List<Preparation> filteredPreparations = new ArrayList<>();

        for (Map.Entry<String, Identifiable> entry : store.entrySet()) {
            // first filter on the class
            if (Preparation.class.equals(entry.getValue().getClass())) {
                Preparation preparation = (Preparation) entry.getValue();
                // second filter on the dataset id
                if (dataSetId.equals(preparation.getDataSetId())) {
                    filteredPreparations.add(preparation);
                }
            }
        }
        return filteredPreparations;
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

    @Override
    public void remove(Identifiable object) {
        if (object == null) {
            return;
        }
        store.remove(object.id());
    }
}
