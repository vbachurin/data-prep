package org.talend.dataprep.api.service.command.info;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.info.ManifestInfo;
import org.talend.dataprep.info.Version;
import org.talend.dataprep.api.service.command.common.GenericCommand;

import com.netflix.hystrix.HystrixCommandGroupKey;

@Component
@Scope("request")
public class VersionCommand extends GenericCommand<InputStream> {

    public static final HystrixCommandGroupKey VERSION_GROUP = HystrixCommandGroupKey.Factory.asKey("version"); //$NON-NLS-1$

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    private final String serviceName;

    private VersionCommand(HttpClient client, String serviceName) {
        super(VERSION_GROUP, client);
        this.serviceName = serviceName;
    }

    protected InputStream run() {
        try {
            ManifestInfo manifestInfo = ManifestInfo.getUniqueInstance();
            final ArrayList<Version> usedServiceVersions = new ArrayList<>();
            usedServiceVersions.add(new Version(manifestInfo.getVersionId(), manifestInfo.getBuildId(), serviceName));
            String string = builder.build().writeValueAsString(usedServiceVersions);
            return IOUtils.toInputStream(string);
        } catch (Exception exc) {
            return null;
        }
    }
}
