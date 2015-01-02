package org.talend.dataprep.dataset.store.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.talend.dataprep.dataset.objects.DataSetMetadata;

public interface MongoDBRepository extends MongoRepository<DataSetMetadata, String> {
}
