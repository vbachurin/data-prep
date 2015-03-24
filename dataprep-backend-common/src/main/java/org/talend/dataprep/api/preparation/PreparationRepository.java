package org.talend.dataprep.api.preparation;

import java.util.Collection;

public interface PreparationRepository {

    void add(Object object);

    <T extends Object> T get(String id, Class<T> clazz);

    <T extends Object> Collection<T> listAll(Class<T> clazz);

    void clear();
}
