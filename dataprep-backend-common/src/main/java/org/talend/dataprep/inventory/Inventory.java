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

import java.io.Serializable;
import java.util.List;

import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.preparation.Preparation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class groups all the contains of a folder. I contains all the datasets, the preparations and the sub-folders
 * recursively contained TODO: This class duplicates in some sense FolderContent. We must find a way to only use folder
 * inventory.
 */
public class Inventory implements Serializable {

    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    /**
     * children folders
     */
    @JsonProperty("folders")
    private List<FolderInfo> folders;

    @JsonProperty("datasets")
    private List<DataSetMetadata> datasets;

    @JsonProperty("preparations")
    private List<Preparation> preparations;

    public Inventory() {
        // no op
    }

    public Inventory(List<DataSetMetadata> datasets, List<FolderInfo> folders, List<Preparation> preparations) {
        this.datasets = datasets;
        this.folders = folders;
        this.preparations = preparations;
    }

    public List<DataSetMetadata> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DataSetMetadata> datasets) {
        this.datasets = datasets;
    }

    public List<FolderInfo> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderInfo> folders) {
        this.folders = folders;
    }

    public List<Preparation> getPreparations() {
        return preparations;
    }

    public void setPreparations(List<Preparation> preparations) {
        this.preparations = preparations;
    }

    @Override
    public String toString() {
        return "FolderContent{" + "datasets=" + datasets + ", preparations=" + preparations + ", folders=" + folders + '}';
    }
}