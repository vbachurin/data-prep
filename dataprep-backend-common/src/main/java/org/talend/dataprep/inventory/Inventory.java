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
import java.util.Collections;
import java.util.List;

import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.PreparationDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class permits to group folders, preparations and data sets. It may be useful to list the content of a folder
 * either recursively or not.
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
    private List<Preparation> preparations;

    // For Jackson Marshaller/Un-marshaller
    public Inventory() {
    }

    /**
     * Creates an inventory with specified data sets and folders and preparations.
     * @param datasets the list of data sets of the inventory
     * @param folders the list of folders of the inventory
     * @param preparations the list of preparations of the inventory
     */
    public Inventory(List<DatasetMetadataInfo> datasets, List<FolderInfo> folders, List<Preparation> preparations) {
        this.datasets = datasets;
        this.folders = folders;
        this.preparations = preparations;
    }

    /**
     * Creates an inventory with specified data sets and folders. The preparation list is considered as empty.
     * 
     * @param datasets the list of data sets of the inventory
     * @param folders the list of folders of the inventory
     */
    public Inventory(List<DatasetMetadataInfo> datasets, List<FolderInfo> folders) {

        this.datasets = datasets;
        this.folders = folders;
        this.preparations = Collections.emptyList();
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