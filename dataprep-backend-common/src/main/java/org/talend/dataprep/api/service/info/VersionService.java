package org.talend.dataprep.api.service.info;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.info.ManifestInfo;
import org.talend.dataprep.info.Version;
import org.talend.dataprep.metrics.Timed;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
//@API(value = {""}, basePath = "/api", description = "Get the version of the running jar")
public class VersionService {

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @RequestMapping(value = {"/datasets/version", "/transform/version", "/preparations/version"}, method = GET)
    @ApiOperation(value = "Get the version of the running API", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Version version(String serviceName) {
        ManifestInfo manifestInfo = ManifestInfo.getUniqueInstance();
        return new Version(manifestInfo.getVersionId(), manifestInfo.getBuildId(), serviceName);
    }
}