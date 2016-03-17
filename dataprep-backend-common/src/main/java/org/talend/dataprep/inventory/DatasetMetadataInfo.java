// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.inventory;

import org.talend.dataprep.api.dataset.DataSetMetadata;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * This class contains useful information about data set. It complements the data set with some information about its
 * containing folder.
 */
public class DatasetMetadataInfo {

    @JsonUnwrapped
    private DataSetMetadata metadata;

    private String path;

    // For Jackson Marshaller/Un-marshaller
    public DatasetMetadataInfo() {
    }

    public DatasetMetadataInfo(DataSetMetadata metadata, String path) {
        this.metadata = metadata;
        this.path = path;
    }

    public DataSetMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DataSetMetadata metadata) {
        this.metadata = metadata;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
