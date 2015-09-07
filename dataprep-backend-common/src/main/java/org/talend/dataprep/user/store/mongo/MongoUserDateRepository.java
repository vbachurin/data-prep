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
package org.talend.dataprep.user.store.mongo;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.user.UserData;
import org.talend.dataprep.user.store.UserDataRepository;

/**
 * Mongo db user data repository implementation.
 */
@Component
@ConditionalOnProperty(name = "user.data.store", havingValue = "mongodb", matchIfMissing = false)
public class MongoUserDateRepository implements UserDataRepository {

    /** Spring interface for mongo db. */
    @Autowired
    private UserDataMongoDbRepo repository;

    /**
     * @see UserDataRepository#get(String)
     */
    @Override
    public UserData get(String userId) {
        return repository.findOne(userId);
    }

    /**
     * @see UserDataRepository#save(UserData)
     */
    @Override
    public void save(UserData userData) {
        repository.save(Collections.singleton(userData));
    }

    /**
     * @see UserDataRepository#remove(String)
     */
    @Override
    public void remove(String userId) {
        repository.delete(userId);
    }

    /**
     * @see UserDataRepository#clear()
     */
    @Override
    public void clear() {
        repository.deleteAll();
    }

}
