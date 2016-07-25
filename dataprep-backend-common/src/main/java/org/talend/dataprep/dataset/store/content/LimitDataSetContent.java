package org.talend.dataprep.dataset.store.content;

import java.io.InputStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;

@Component
class LimitDataSetContent implements DataSetContentLimit {

    @Value("${dataset.records.limit}")
    private long limit;

    @Override
    public DataSetContentStore get(DataSetContentStore contentStore) {
        return new LimitDataSetContentStore(contentStore);
    }

    @Override
    public boolean limitContentSize() {
        return true;
    }

    private class LimitDataSetContentStore extends DataSetContentStore {

        private final DataSetContentStore store;

        private LimitDataSetContentStore(DataSetContentStore store) {
            this.store = store;
        }

        @Override
        public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
            store.storeAsRaw(dataSetMetadata, dataSetContent);
        }

        @Override
        public InputStream get(DataSetMetadata dataSetMetadata) {
            return store.get(dataSetMetadata, limit);
        }

        @Override
        public Stream<DataSetRow> stream(DataSetMetadata dataSetMetadata, long limit) {
            return stream(dataSetMetadata);
        }

        @Override
        public Stream<DataSetRow> stream(DataSetMetadata dataSetMetadata) {
            Stream<DataSetRow> dataSetRowStream = super.stream(dataSetMetadata, limit);
            // deal with dataset size limit (ignored if limit is <= 0)
            return dataSetRowStream.limit(limit);
        }

        @Override
        public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {
            return store.getAsRaw(dataSetMetadata, limit);
        }

        @Override
        public InputStream getAsRaw(DataSetMetadata dataSetMetadata, long limit) {
            return getAsRaw(dataSetMetadata);
        }

        @Override
        protected InputStream get(DataSetMetadata dataSetMetadata, long limit) {
            return get(dataSetMetadata);
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
