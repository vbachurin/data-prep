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
 * created by sgandon on 16 juin 2015 Detailled comment
 *
 */
@Component
@ConditionalOnProperty(name = "user.data.store", havingValue = "mongodb", matchIfMissing = false)
public class MongoUserDateRepository implements UserDataRepository {

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
