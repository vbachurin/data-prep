package org.talend.dataprep.configuration;

import java.io.File;
import java.net.URI;

import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.exception.TDPException;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.exception.error.CommonErrorCodes;

@Configuration
@ConditionalOnProperty(name = "hdfs.location")
public class HDFS {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFS.class);

    @Value("${hdfs.location}")
    private String hdfsLocation;

    @Bean
    public FileSystem getHDFSFileSystem() {
        try {
            FileSystem fileSystem = FileSystem.get(new URI(hdfsLocation), new org.apache.hadoop.conf.Configuration());
            LOGGER.info("HDFS file system: {} ({}).", fileSystem.getClass(), new File(fileSystem.getUri()).getAbsolutePath());
            return fileSystem;
        } catch (Exception e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_CONNECT_TO_HDFS, e, ExceptionContext.build().put("location",
                    hdfsLocation));
        }
    }
}
