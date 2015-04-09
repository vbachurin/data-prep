package org.talend.dataprep.preparation.store.mongo;

import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.preparation.Identifiable;
import org.talend.dataprep.api.preparation.PreparationActions;
import org.talend.dataprep.api.preparation.PreparationRepository;
import org.talend.dataprep.api.preparation.Step;

public class MongoDBPreparationRepository implements PreparationRepository {

    @Autowired
    PreparationStorage store;

    @Override
    public void add(Identifiable object) {
        store.save(object);
    }

    @Override
    public <T extends Identifiable> T get(String id, Class<T> clazz) {
        if (id == null) {
            return null;
        }
        Object object = store.findOne(id);
        if (object == null) {
            return null;
        }
        if (clazz == null) {
            return (T) object;
        }
        if (clazz.isAssignableFrom(object.getClass())) {
            return clazz.cast(object);
        } else {
            return null;
        }
    }

    @Override
    public <T extends Identifiable> Collection<T> listAll(Class<T> clazz) {
        return (Collection<T>) store.findAll(clazz.getName());
    }

    @Override
    public void clear() {
        store.deleteAll();
        add(ROOT_CONTENT);
        add(ROOT_STEP);
    }

    @Override
    public void remove(Identifiable object) {
        if (object == null) {
            return;
        }
        store.delete(object.getClass().getName(), object.id());
    }

    @PostConstruct
    public void init() {
        add(Step.ROOT_STEP);
        add(PreparationActions.ROOT_CONTENT);
    }
}
