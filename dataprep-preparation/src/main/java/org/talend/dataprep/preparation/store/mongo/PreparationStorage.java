package org.talend.dataprep.preparation.store.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.talend.dataprep.api.preparation.Identifiable;

public interface PreparationStorage extends MongoRepository<Identifiable, String> {

    @Query("{ '_class' : ?0 }")
    List<Identifiable> findAll(String className);
}
