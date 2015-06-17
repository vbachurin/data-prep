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
package org.talend.dataprep.store;

import javax.validation.constraints.NotNull;

import org.talend.dataprep.api.user.UserData;

/**
 * storage interface for the user data
 *
 */
public interface UserDataRepository {

    /**
     * get the {@link UserData} for the given userId from the current storage
     * 
     * @param userId, the identifier of the user
     * @return the {@link UserData} for the given userId
     */
    @NotNull
    UserData getUserData(String userId);

    /**
     * store the given {@link UserData}
     * 
     * @param userData the {@link UserData} to store
     */
    void setUserData(UserData userData);

    /**
     * remove the userData associated with the userId
     * 
     * @param userId, the id of the user have it's UserData removed
     */
    void remove(String userId);

    /**
     * <p>
     * Removes all {@link UserData} in this repository. Repository does not provide rollback operation for this, use it
     * with care!
     * </p>
     */
    void clear();

}
