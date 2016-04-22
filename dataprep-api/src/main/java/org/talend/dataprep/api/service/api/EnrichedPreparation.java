//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.api.service.api;

import java.io.Serializable;
import java.util.Objects;

import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.preparation.Preparation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple bean used to display a preparation and a summary of its related dataset.
 */
@JsonIgnoreProperties({"dataSetId"})
public class EnrichedPreparation extends Preparation {

    /** The dataset metadata to summurize. */
    @JsonProperty("dataset")
    private DataSetMetadataSummary summary;

    /**
     * Default constructor.
     *
     * @param preparation the preparation to display.
     * @param dataSetMetadata the dataset metadata to summurize.
     */
    public EnrichedPreparation(Preparation preparation, DataSetMetadata dataSetMetadata) {
        super();
        this.setDataSetId(preparation.getDataSetId());
        this.setAuthor(preparation.getAuthor());
        this.setName(preparation.getName());
        this.setCreationDate(preparation.getCreationDate());
        this.setLastModificationDate(preparation.getLastModificationDate());
        this.setHeadId(preparation.getHeadId());
        this.setAppVersion(preparation.getAppVersion());
        this.setSteps(preparation.getSteps());

        this.summary = new DataSetMetadataSummary(dataSetMetadata);
    }

    /**
     * @see Object#equals(Object)  
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        EnrichedPreparation that = (EnrichedPreparation) o;
        return Objects.equals(summary, that.summary);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), summary);
    }

    /**
     * @return the Summary
     */
    public DataSetMetadataSummary getSummary() {
        return summary;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "EnrichedPreparation{" +
                "preparation id=" + getId() +'\''+
                ", summary=" + summary +
                '}';
    }

    /**
     * Inner class that summurize a dataset metadata.
     */
    public static class DataSetMetadataSummary implements Serializable {


        private static final long serialVersionUID = 1L;

        private String dataSetId;
        private String dataSetName;
        private long dataSetNbRow;

        public DataSetMetadataSummary(DataSetMetadata metadata) {
            this.dataSetId = metadata.getId();
            this.dataSetName = metadata.getName();
            this.dataSetNbRow = metadata.getContent().getNbRecords();
        }

        /**
         * @return the DataSetId
         */
        public String getDataSetId() {
            return dataSetId;
        }

        /**
         * @return the DataSetName
         */
        public String getDataSetName() {
            return dataSetName;
        }

        /**
         * @return the DataSetNbRow
         */
        public long getDataSetNbRow() {
            return dataSetNbRow;
        }

        /**
         * @see Object#toString()
         */
        @Override
        public String toString() {
            return "DataSetMetadataSummary{" +
                    "dataSetId='" + dataSetId + '\'' +
                    ", dataSetName='" + dataSetName + '\'' +
                    ", dataSetNbRow=" + dataSetNbRow +
                    '}';
        }
    }
}
