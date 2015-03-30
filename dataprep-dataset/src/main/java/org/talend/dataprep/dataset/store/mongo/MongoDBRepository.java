package org.talend.dataprep.dataset.store.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.talend.dataprep.api.dataset.DataSetMetadata;

public interface MongoDBRepository extends MongoRepository<DataSetMetadata, String> {
}
