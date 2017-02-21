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

package org.talend.services.dataprep.api;

import java.util.concurrent.Callable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.service.settings.AppSettings;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.security.PublicAPI;

/**
 * App settings API
 */
@Service(name = "dataprep.AppSettingsAPI")
public interface AppSettingsAPI {

    /**
     * Returns the app settings to configure frontend components
     */
    @RequestMapping(value = "/api/settings", method = RequestMethod.GET)
    @Timed
    @PublicAPI
    Callable<AppSettings> getSettings();
}
