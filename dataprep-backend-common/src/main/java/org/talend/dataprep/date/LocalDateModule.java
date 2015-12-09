package org.talend.dataprep.date;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Jackson module used to register LocalDate deserializer
 */
@Component
public class LocalDateModule extends SimpleModule {
    /**
     * Constructor
     */
    public LocalDateModule() {
        super(LocalDateModule.class.getName(), new Version(1, 0, 0, null, null, null));
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
    }
}
