package org.talend.dataprep.api.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.service.command.info.VersionCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.info.ManifestInfo;
import org.talend.dataprep.info.Version;
import org.talend.dataprep.metrics.Timed;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@RestController
@Api(value = "api", basePath = "/api", description = "Get the version of the running jar")
public class VersionServiceAPI extends APIService {

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @Value("${transformation.service.url}")
    protected String transformationServiceUrl;

    @Value("${dataset.service.url}")
    protected String datasetServiceUrl;

    @Value("${preparation.service.url}")
    protected String preparationServiceUrl;

    @Autowired(required = true)
    private HttpServletRequest request;

    @RequestMapping(value = "/api/version", method = GET)
    @ApiOperation(value = "Get the version of the running API", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    /**
     * Returns all the versions of the different services (api, dataset, preparation and transforamtion).
     * 
     * @return an array of service versions
     */
    public Version[] allVersions() {
        Version[] versions = new Version[4];
        ManifestInfo manifestInfo = ManifestInfo.getUniqueInstance();

        String serviceUrl = "http://" + request.getLocalAddr() + ":" + request.getLocalPort();

        versions[0] = new Version(manifestInfo.getVersionId(), manifestInfo.getBuildId(), "API: (" + serviceUrl + ")");
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
        HttpClient client = getClient();
        HystrixCommand<InputStream> versionCommand = getCommand(VersionCommand.class, client, serviceUrl, serviceName);
        try (InputStream content = versionCommand.execute()) {
            return builder.build().reader(Version.class).readValue(content);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_GET_SERVICE_VERSION, e);
        }
    }

}