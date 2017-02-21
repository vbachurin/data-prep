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

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.service.command.info.VersionCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.info.ManifestInfo;
import org.talend.dataprep.info.Version;
import org.talend.services.dataprep.api.VersionServiceAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@ServiceImplementation
public class VersionServiceAPIImpl extends APIService implements VersionServiceAPI {

    @Autowired
    ObjectMapper mapper;

    @Value("${transformation.service.url}")
    protected String transformationServiceUrl;

    @Value("${dataset.service.url}")
    protected String datasetServiceUrl;

    @Value("${preparation.service.url}")
    protected String preparationServiceUrl;

    @Override
    public Version[] allVersions() {
        Version[] versions = new Version[4];
        ManifestInfo manifestInfo = ManifestInfo.getInstance();

        versions[0] = new Version(manifestInfo.getVersionId(), manifestInfo.getBuildId(), "API");
        versions[1] = callVersionService(datasetServiceUrl, "DATASET");
        versions[2] = callVersionService(preparationServiceUrl, "PREPARATION");
        versions[3] = callVersionService(transformationServiceUrl, "TRANSFORMATION");

        return versions;
    }

    /**
     * Call the version service on the given service: dataset, preparation or transformation.
     *
     * @param serviceName the name of the service
     * @return the version of the called service
     */
    private Version callVersionService(String serviceUrl, String serviceName) {
        HystrixCommand<InputStream> versionCommand = getCommand(VersionCommand.class, serviceUrl);
        try (InputStream content = versionCommand.execute()) {
            final Version version = mapper.readerFor(Version.class).readValue(content);
            version.setServiceName(serviceName);
            return version;
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_GET_SERVICE_VERSION, e);
        }
    }

}
