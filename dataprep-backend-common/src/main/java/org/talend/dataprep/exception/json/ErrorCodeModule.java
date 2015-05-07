package org.talend.dataprep.exception.json;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class ErrorCodeModule extends SimpleModule {

    public ErrorCodeModule() {
        super(JsonErrorCode.class.getName(), new Version(1, 0, 0, null, null, null));
    }
}
