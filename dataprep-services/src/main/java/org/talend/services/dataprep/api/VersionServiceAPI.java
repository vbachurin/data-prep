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

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RequestMapping;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.info.Version;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.security.PublicAPI;

@Service(name = "dataprep.VersionServiceAPI")
public interface VersionServiceAPI {

    /**
     * Returns all the versions of the different services (api, dataset, preparation and transformation).
     *
     * @return an array of service versions
     */
    @RequestMapping(value = "/api/version", method = GET)
    @Timed
    @PublicAPI
    Version[] allVersions();
}
