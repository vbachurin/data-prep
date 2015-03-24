package org.talend.dataprep.api.preparation.store.mongo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.preparation.Object;
import org.talend.dataprep.api.preparation.PreparationRepository;

public class MongoDBPreparationRepository implements PreparationRepository {

    @Autowired
    PreparationStorage store;

    @Override
    public void add(Object object) {
        store.save(object);
    }

    @Override
    public <T extends Object> T get(String id, Class<T> clazz) {
        Object object = store.findOne(id);
        if (object == null) {
            return null;
        }
        if (clazz.isAssignableFrom(object.getClass())) {
            return clazz.cast(object);
        } else {
            return null;
        }
    }

    @Override
    public <T extends Object> Collection<T> listAll(Class<T> clazz) {
        return (Collection<T>) store.findAll(clazz.toString());
    }

    @Override
    public void clear() {
        store.deleteAll();
    }
}
