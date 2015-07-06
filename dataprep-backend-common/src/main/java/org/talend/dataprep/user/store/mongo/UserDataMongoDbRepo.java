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

import org.springframework.data.mongodb.repository.MongoRepository;
import org.talend.dataprep.api.user.UserData;

public interface UserDataMongoDbRepo extends MongoRepository<UserData, String> {
    // interface to inject the Mongo Repo for User Data
}