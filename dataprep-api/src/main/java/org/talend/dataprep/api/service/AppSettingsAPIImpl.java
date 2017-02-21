// ============================================================================
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

package org.talend.dataprep.api.service;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.service.settings.AppSettings;
import org.talend.dataprep.api.service.settings.AppSettingsService;
import org.talend.services.dataprep.api.AppSettingsAPI;

@ServiceImplementation
public class AppSettingsAPIImpl extends APIService implements AppSettingsAPI {

    @Autowired
    private AppSettingsService appSettingsService;

    @Override
    public Callable<AppSettings> getSettings() {
        return () -> appSettingsService.getSettings();
    }
}
