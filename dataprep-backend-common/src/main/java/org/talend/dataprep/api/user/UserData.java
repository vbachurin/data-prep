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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Holds data related to a single user.
 */
@JsonRootName("userdata")
public class UserData implements Serializable {

    /** Favorites datasets. */
    @JsonProperty(value = "favoritedDS", required = false)
    Set<String> favoritesDatasets = new HashSet<>();

    /** User id. */
    @JsonProperty(value = "userId", required = true)
    @Id
    String userId;

    /**
     * Constructor.
     * 
     * @param userId the used id.
     */
    public UserData(String userId) {
        this.userId = userId;
    }

    /**
     * Getter for favoritesDatasets.
     * 
     * @return the list of favorites DataSet Ids for the current user.
     */
    public Set<String> getFavoritesDatasets() {
        return this.favoritesDatasets;
    }

    /**
     * Sets the favoritesDatasets.
     * 
     * @param favoritesDatasets set the favorites DataSets for the given user.
     */
    public void setFavoritesDatasets(Set<String> favoritesDatasets) {
        this.favoritesDatasets = favoritesDatasets;
    }

    /**
     * add a DataSet into the favorites list
     * 
     * @param dataSetId, the favorite to be dataset.
     */
    public void addFavoriteDataset(String dataSetId) {
        this.favoritesDatasets.add(dataSetId);
    }

    /**
     * Getter for userId.
     * 
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "UserData{" + "favoritesDatasets=" + favoritesDatasets + ", userId='" + userId + '\'' + '}';
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserData userData = (UserData) o;
        return Objects.equals(favoritesDatasets, userData.favoritesDatasets) && Objects.equals(userId, userData.userId);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(favoritesDatasets, userId);
    }

}
