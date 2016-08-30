package org.talend.dataprep.dataset.store.metadata;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.filter.ObjectPredicateVisitor;
import org.talend.dataprep.lock.DistributedLock;
import org.talend.tql.parser.Tql;

public abstract class ObjectDataSetMetadataRepository extends DataSetMetadataRepositoryAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectDataSetMetadataRepository.class);

    protected abstract Stream<DataSetMetadata> source();

    @Override
    public boolean exist(String filter) {
        final Predicate<Object> accept = (Predicate<Object>) Tql.parse(filter)
                .accept(new ObjectPredicateVisitor(DataSetMetadata.class));
        return source().filter(accept).findAny().isPresent();
    }

    @Override
    public Stream<DataSetMetadata> list() {
        return source();
    }

    @Override
    public Stream<DataSetMetadata> list(String filter) {
        final Predicate<Object> accept = (Predicate<Object>) Tql.parse(filter)
                .accept(new ObjectPredicateVisitor(DataSetMetadata.class));
        return source().filter(accept);
    }

    @Override
    public int size() {
        return (int) source().count();
    }

    @Override
    public void clear() {
        // Remove all data set (but use lock for remaining asynchronous processes).
        list().forEach(m -> {
            if (m != null) {
                final DistributedLock lock = createDatasetMetadataLock(m.getId());
                try {
                    lock.lock();
                    remove(m.getId());
                } finally {
                    lock.unlock();
                }
            }
        });
        LOGGER.debug("dataset metadata repository cleared.");
    }

}
