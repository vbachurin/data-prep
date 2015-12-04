package org.talend.dataprep.api.service.info;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.info.ManifestInfo;
import org.talend.dataprep.info.Version;
import org.talend.dataprep.metrics.Timed;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@Api(value = "version", basePath = "/version", description = "versions of running application")
public class VersionService {

    @Autowired(required = true)
    private HttpServletRequest request;

    @RequestMapping(value = "/version", method = GET)
    @ApiOperation(value = "Get the version of the running service", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Version version(String serviceName) {
        String serviceUrl = "http://" + request.getLocalAddr() + ":" + request.getLocalPort();
        ManifestInfo manifestInfo = ManifestInfo.getUniqueInstance();
        return new Version(manifestInfo.getVersionId(), manifestInfo.getBuildId(), serviceName + ": (" + serviceUrl + ")");
    }
}