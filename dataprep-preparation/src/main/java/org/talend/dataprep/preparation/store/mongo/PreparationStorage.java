package org.talend.dataprep.preparation.store.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.talend.dataprep.api.preparation.Identifiable;

public interface PreparationStorage extends MongoRepository<Identifiable, String> {

    @Query("{ '_class' : '?0' }")
    List<Identifiable> findAll(String className);

    @Query("{ '_class' : '?0',  'dataSetId' : '?1' }")
    List<Identifiable> findByDataSet(String className, String dataSetId);

    @Query(value = "{ '_class' : '?0', '_id' : '?1' }", delete = true)
    Long delete(String className, String id);
}
