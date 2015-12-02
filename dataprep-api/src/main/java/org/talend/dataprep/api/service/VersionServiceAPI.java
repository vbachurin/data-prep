package org.talend.dataprep.api.service;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.info.Version;
import org.talend.dataprep.api.service.command.info.VersionCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.metrics.Timed;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@Api(value = "api", basePath = "/api", description = "Get the version of the running jar")
public class VersionServiceAPI extends APIService {

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @RequestMapping(value = "/api/version", method = GET)
    @ApiOperation(value = "Get the version of the running API", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ArrayList<Version> version(final HttpServletResponse response) {
        response.setHeader("Content-Type", APPLICATION_JSON_VALUE);
        HttpClient client = getClient();
        HystrixCommand<InputStream> versionCommand = getCommand(VersionCommand.class, client, "API");
        try (InputStream content = versionCommand.execute()) {
            return builder.build().reader(ArrayList.class).readValue(content);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}