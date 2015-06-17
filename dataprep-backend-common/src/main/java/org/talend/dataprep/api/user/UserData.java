// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.api.user;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.talend.dataprep.api.dataset.DataSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Holds data related to a single user.
 *
 */
@JsonRootName("userdata")
public class UserData {

    @JsonProperty(value = "favoritedDS", required = false)
    Set<DataSet> favoritesDatasets = new HashSet<>();

    /**
     * Getter for favoritesDatasets.
     * 
     * @return the list of favorites DataSet for the current user.
     */
    public Set<DataSet> getFavoritesDatasets() {
        return this.favoritesDatasets;
    }

    /**
     * Sets the favoritesDatasets.
     * 
     * @param favoritesDatasets set the favorites DataSets for the given user.
     */
    public void setFavoritesDatasets(Set<DataSet> favoritesDatasets) {
        this.favoritesDatasets = favoritesDatasets;
    }

    /**
     * add a DataSet into the favorites list
     * 
     * @param dataSet, the favorite to be dataset.
     */
    public void addFavoriteDataset(DataSet dataSet) {
        this.favoritesDatasets.add(dataSet);
    }

    @JsonProperty(value = "userId", required = true)
    @Id
    String userId;

    /**
     * Getter for userId.
     * 
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    public UserData(String userId) {
        this.userId = userId;
    }
}
