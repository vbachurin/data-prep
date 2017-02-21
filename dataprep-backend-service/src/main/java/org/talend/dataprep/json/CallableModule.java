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

package org.talend.dataprep.json;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class CallableModule extends SimpleModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallableModule.class);

    @Autowired
    @Lazy
    ObjectMapper mapper;

    /**
     * Register the serializer and deserializer.
     */
    @PostConstruct
    private void registerSerializers() {
        addSerializer(Callable.class, new JsonSerializer<Callable>() {

            @Override
            public void serialize(Callable callable, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                try {
                    mapper.writeValue(jsonGenerator, callable.call());
                } catch (Exception e) {
                    LOGGER.error("Unable to write callable value.", e);
                }
            }
        });
        addDeserializer(Callable.class, new JsonDeserializer<Callable>() {

            @Override
            public Callable deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                return jsonParser::getCurrentValue;
            }
        });
    }
}
