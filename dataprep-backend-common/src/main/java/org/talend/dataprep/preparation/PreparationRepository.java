package org.talend.dataprep.preparation;

import java.util.Set;

public interface PreparationRepository {

    void add(Object object);

    <T extends Object> T get(String id, Class<T> clazz);

    <T extends Object> Set<T> listAll(Class<T> clazz);

    void clear();
}
