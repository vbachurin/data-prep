package org.talend.dataprep.dataset.store.content;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;

import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

@Component
class LimitDataSetContent implements DataSetContentLimit {

    @Override
    public DataSetContentStore get(DataSetContentStore contentStore) {
        return LimitDataSetContentStore.get(contentStore);
    }

    @Override
    public boolean limitContentSize() {
        return true;
    }

    private static class LimitDataSetContentStore extends DataSetContentStore {

        private final DataSetContentStore store;

        private LimitDataSetContentStore(DataSetContentStore store) {
            this.store = store;
        }

        public static DataSetContentStore get(DataSetContentStore store) {
            return new LimitDataSetContentStore(store);
        }

        @Override
        public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
            store.storeAsRaw(dataSetMetadata, dataSetContent);
        }

        @Override
        public InputStream get(DataSetMetadata dataSetMetadata) {
            return store.get(dataSetMetadata);
        }

        @Override
        public Stream<DataSetRow> stream(DataSetMetadata dataSetMetadata) {
            Stream<DataSetRow> dataSetRowStream = super.stream(dataSetMetadata);
            // deal with dataset size limit (ignored if limit is <= 0)
            final Optional<Long> limit = dataSetMetadata.getContent().getLimit();
            if (limit.isPresent() && limit.get() > 0) {
                dataSetRowStream = dataSetRowStream.limit(limit.get());
            }
            return dataSetRowStream;
        }

        @Override
        public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {
            return store.getAsRaw(dataSetMetadata);
        }

        @Override
        public void delete(DataSetMetadata dataSetMetadata) {
            store.delete(dataSetMetadata);
        }

        @Override
        public void clear() {
            store.clear();
        }
    }
}
