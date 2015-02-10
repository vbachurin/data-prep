package org.talend.dataprep.dataset.store.mongo;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

public class MongoDataSetMetadataRepository implements DataSetMetadataRepository {

    @Autowired
    MongoDBRepository repository;

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
        repository.deleteAll();
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
}
