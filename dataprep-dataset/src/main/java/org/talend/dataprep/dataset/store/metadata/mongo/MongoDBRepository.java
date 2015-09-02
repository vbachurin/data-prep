package org.talend.dataprep.dataset.store.metadata.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.talend.dataprep.api.dataset.DataSetMetadata;

/**
 * Spring way for mongo repository dedicated for DataSetMetadata.
 */
public interface MongoDBRepository extends MongoRepository<DataSetMetadata, String> {
}
