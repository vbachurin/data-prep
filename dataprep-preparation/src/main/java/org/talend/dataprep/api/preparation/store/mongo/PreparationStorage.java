package org.talend.dataprep.api.preparation.store.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.talend.dataprep.api.preparation.Object;

public interface PreparationStorage extends MongoRepository<Object, String> {

    @Query("{ '_class' : ?0 }")
    List<Object> findAll(String className);
}
