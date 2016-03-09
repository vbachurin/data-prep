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

import org.talend.dataprep.api.folder.Folder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * This class decorates (it is not a decorator) the folder with more information: the number of data sets and
 * preparations.
 *
 */
public class FolderInfo {

    @JsonUnwrapped
    Folder folder;

    /**
     * The number of data sets contained in the folder
     */
    @JsonProperty
    private int nbDatasets;

    /**
     * The number of data sets contained in the folder
     */
    @JsonProperty
    private int nbPreparations;

    public FolderInfo() {
    }

    public FolderInfo(Folder folder, int nbDatasets, int nbPreparation) {
        this.folder = folder;
        this.nbDatasets = nbDatasets;
        this.nbPreparations = nbPreparation;
    }

    public Folder getFolder() {
        return folder;
    }

    public int getNbDatasets() {
        return nbDatasets;
    }

    public void setNbDatasets(int nbDatasets) {
        this.nbDatasets = nbDatasets;
    }

    public int getNbPreparations() {
        return nbPreparations;
    }

    public void setNbPreparations(int nbPreparations) {
        this.nbPreparations = nbPreparations;
    }

    public String getPath() {
        return folder.getPath();
    }

    public String getName() {
        return folder.getName();
    }

    public long getCreationDate() {
        return folder.getCreationDate();
    }

    public long getLastModificationDate() {
        return folder.getLastModificationDate();
    }
}