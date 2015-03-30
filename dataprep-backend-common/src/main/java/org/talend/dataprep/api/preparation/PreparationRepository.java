package org.talend.dataprep.api.preparation;

import java.util.Collection;

public interface PreparationRepository {

    void add(Identifiable object);

    <T extends Identifiable> T get(String id, Class<T> clazz);

    <T extends Identifiable> Collection<T> listAll(Class<T> clazz);

    void clear();
}
