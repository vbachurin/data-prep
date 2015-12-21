package org.talend.dataprep.date;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Jackson module used to register LocalDateTime deserializer
 */
@Component
public class LocalDateTimeModule extends SimpleModule {
    /**
     * Constructor
     */
    public LocalDateTimeModule() {
        super(LocalDateTimeModule.class.getName(), new Version(1, 0, 0, null, null, null));
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
    }
}
