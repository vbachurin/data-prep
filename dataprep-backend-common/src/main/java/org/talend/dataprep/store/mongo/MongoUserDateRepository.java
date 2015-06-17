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
package org.talend.dataprep.store.mongo;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.talend.dataprep.api.user.UserData;
import org.talend.dataprep.store.UserDataRepository;

/**
 * created by sgandon on 16 juin 2015 Detailled comment
 *
 */
public class MongoUserDateRepository implements UserDataRepository {

    public interface UserDataMongoDbRepo extends MongoRepository<UserData, String> {
        // interface to inject the Mongo Repo below
    }

    @Autowired
    UserDataMongoDbRepo repository;

    @Override
    public UserData getUserData(String userId) {
        return repository.findOne(userId);
    }

    @Override
    public void setUserData(UserData userData) {
        repository.save(Collections.singleton(userData));

    }

    @Override
    public void remove(String userId) {
        repository.delete(userId);
    }

    @Override
    public void clear() {
        repository.deleteAll();
    }

}
