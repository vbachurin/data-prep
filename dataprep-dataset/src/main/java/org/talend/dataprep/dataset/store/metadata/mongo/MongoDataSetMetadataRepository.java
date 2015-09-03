package org.talend.dataprep.dataset.store.metadata.mongo;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepositoryAdapter;
import org.talend.dataprep.lock.DistributedLock;

/**
 * MongoDB implementation of the DatasetMetadataRepository.
 */
@Component
@ConditionalOnProperty(name = "dataset.metadata.store", havingValue = "mongodb")
public class MongoDataSetMetadataRepository extends DataSetMetadataRepositoryAdapter {

    /** Spring Mongo DB repository. */
    @Autowired
    private MongoDBRepository repository;

    /**
     * @see DataSetMetadataRepository#list()
     */
    @Override
    public Iterable<DataSetMetadata> list() {
        return repository.findAll();
    }

    /**
     * @see DataSetMetadataRepository#add(DataSetMetadata)
     */
    @Override
    public void add(DataSetMetadata dataSetMetadata) {
        repository.save(Collections.singleton(dataSetMetadata));
    }

    /**
     * @see DataSetMetadataRepository#clear()
     */
    @Override
    public void clear() {
        // Remove all data set (but use lock for remaining asynchronous processes).
        for (DataSetMetadata metadata : list()) {
            final DistributedLock lock = createDatasetMetadataLock(metadata.getId());
            try {
                lock.lock();
                remove(metadata.getId());
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * @see DataSetMetadataRepository#size()
     */
    @Override
    public int size() {
        return (int) repository.count();
    }

    /**
     * @see DataSetMetadataRepository#get(String)
     */
    @Override
    public DataSetMetadata get(String id) {
        return repository.findOne(id);
    }

    /**
     * @see DataSetMetadataRepository#remove(String)
     */
    @Override
    public void remove(String id) {
        repository.delete(id);
    }

}
