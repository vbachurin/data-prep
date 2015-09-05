package org.talend.dataprep.preparation.store.mongo;

import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Identifiable;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.PreparationActions;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.preparation.store.PreparationRepository;

/**
 * MongoDB implementation of the preparation repository.
 */
@Component
@ConditionalOnProperty(name = "preparation.store", havingValue = "mongodb")
public class MongoDBPreparationRepository implements PreparationRepository {

    /** Spring interface used to access mongodb. */
    @Autowired
    private PreparationStorage store;

    /**
     * Add the root elements in the repository.
     */
    @PostConstruct
    public void init() {
        add(Step.ROOT_STEP);
        add(PreparationActions.ROOT_CONTENT);
    }

    /**
     * @see PreparationRepository#add(Identifiable)
     */
    @Override
    public void add(Identifiable object) {
        store.save(object);
    }

    /**
     * @see PreparationRepository#get(String, Class)
     */
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

    /**
     * @see PreparationRepository#getByDataSet(String)
     */
    @Override
    public Collection<Preparation> getByDataSet(String dataSetId) {

        // defensive programming
        if (StringUtils.isEmpty(dataSetId)) {
            return Collections.emptyList();
        }

        // double cast needed to convert the list of Identifiable into a Preparation one
        return (Collection<Preparation>) (Collection<?>) store.findByDataSet(Preparation.class.getName(), dataSetId);
    }

    /**
     * @see PreparationRepository#listAll(Class)
     */
    @Override
    public <T extends Identifiable> Collection<T> listAll(Class<T> clazz) {
        return (Collection<T>) store.findAll(clazz.getName());
    }

    /**
     * @see PreparationRepository#clear()
     */
    @Override
    public void clear() {
        store.deleteAll();
        add(ROOT_CONTENT);
        add(ROOT_STEP);
    }

    /**
     * @see PreparationRepository#remove(Identifiable)
     */
    @Override
    public void remove(Identifiable object) {
        if (object == null) {
            return;
        }
        store.delete(object.getClass().getName(), object.id());
    }


}
