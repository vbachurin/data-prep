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

import org.talend.dataprep.api.preparation.PreparationDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class groups all the contains of a folder. I contains all the datasets, the preparations and the sub-folders
 * recursively contained
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
    private List<DatasetMetadataInfo> datasets;

    @JsonProperty("preparations")
    private List<PreparationDetails> preparations;

    public Inventory() {
        // no op
    }

    public Inventory(List<DatasetMetadataInfo> datasets, List<FolderInfo> folders, List<PreparationDetails> preparations) {
        this.datasets = datasets;
        this.folders = folders;
        this.preparations = preparations;
    }

    /**
     * Creates a inventory
     * 
     * @param datasets
     * @param folders
     */
    public Inventory(List<DatasetMetadataInfo> datasets, List<FolderInfo> folders) {

        this.datasets = datasets;
        this.folders = folders;
        this.preparations = preparations;
    }

    public List<DatasetMetadataInfo> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DatasetMetadataInfo> datasets) {
        this.datasets = datasets;
    }

    public List<FolderInfo> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderInfo> folders) {
        this.folders = folders;
    }

    public List<PreparationDetails> getPreparations() {
        return preparations;
    }

    public void setPreparations(List<PreparationDetails> preparations) {
        this.preparations = preparations;
    }

    @Override
    public String toString() {
        return "FolderContent{" + "datasets=" + datasets + ", preparations=" + preparations + ", folders=" + folders + '}';
    }
}