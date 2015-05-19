package org.talend.dataprep.api.dataset;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.talend.dataprep.api.dataset.json.DataSetRowStreamDeserializer;
import org.talend.dataprep.api.dataset.json.DataSetRowStreamSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonRootName("dataset")
public class DataSet {

    public static DataSet EMPTY = new DataSet();

    @JsonProperty(value = "metadata", required = false)
    DataSetMetadata metadata;

    @JsonProperty(value = "columns", required = false)
    List<ColumnMetadata> columns = new LinkedList<>();

    @JsonProperty(value = "records", required = false)
    @JsonSerialize(using = DataSetRowStreamSerializer.class)
    @JsonDeserialize(using = DataSetRowStreamDeserializer.class)
    Stream<DataSetRow> records = Stream.of();

    public DataSetMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DataSetMetadata metadata) {
        this.metadata = metadata;
    }

    public Stream<DataSetRow> getRecords() {
        return records;
    }

    public void setRecords(Stream<DataSetRow> records) {
        this.records = records;
    }

    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnMetadata> columns) {
        this.columns = columns;
    }
}
