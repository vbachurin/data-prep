package org.talend.dataprep.dataset.store.mongo;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

public class MongoDataSetMetadataRepository implements DataSetMetadataRepository {

    @Autowired
    MongoDBRepository repository;

    @Autowired
    ApplicationContext appcontext;

    @Override
    public Iterable<DataSetMetadata> list() {
        return repository.findAll();
    }

    @Override
    public void add(DataSetMetadata dataSetMetadata) {
        repository.save(Collections.singleton(dataSetMetadata));
    }

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

    @Override
    public int size() {
        return (int) repository.count();
    }

    @Override
    public DataSetMetadata get(String id) {
        return repository.findOne(id);
    }

    @Override
    public void remove(String id) {
        repository.delete(id);
    }

    @Override
    public DistributedLock createDatasetMetadataLock(String id) {
        return appcontext.getBean(DistributedLock.class, DATASET_LOCK_PREFIX + id);
    }
}
