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
package org.talend.dataprep.store.local;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.api.user.UserData;
import org.talend.dataprep.store.UserDataRepository;

/**
 * created by sgandon on 16 juin 2015 Detailled comment
 *
 */
public class InMemoryUserDataRepository implements UserDataRepository {

    private final Map<String, UserData> store = new HashMap<>();

    @Override
    public UserData getUserData(String userId) {
        return store.get(userId);
    }

    @Override
    public void setUserData(UserData userData) {
        store.put(userData.getUserId(), userData);
    }

    @Override
    public void remove(String userId) {
        store.remove(userId);
    }

    @Override
    public void clear() {
        store.clear();
    }

}
