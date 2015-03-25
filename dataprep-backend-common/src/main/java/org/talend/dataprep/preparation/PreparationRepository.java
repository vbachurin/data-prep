package org.talend.dataprep.preparation;

import java.util.Set;

public interface PreparationRepository {

    void add(Identifiable object);

    <T extends Identifiable> T get(String id, Class<T> clazz);

    <T extends Identifiable> Set<T> listAll(Class<T> clazz);

    void clear();
}
