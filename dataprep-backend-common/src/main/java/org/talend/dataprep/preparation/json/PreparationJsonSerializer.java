package org.talend.dataprep.preparation.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.talend.dataprep.preparation.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
class PreparationJsonSerializer extends JsonSerializer<Preparation> implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void serialize(Preparation preparation, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        {
            generator.writeStringField("id", preparation.id()); //$NON-NLS-1$
            generator.writeStringField("dataSetId", preparation.getDataSetId()); //$NON-NLS-1$
            generator.writeStringField("author", preparation.getAuthor()); //$NON-NLS-1$
            generator.writeNumberField("creationDate", preparation.getCreationDate()); //$NON-NLS-1$
            PreparationRepository versionRepository = getRepository();
            if (versionRepository != null) {
                // Steps
                generator.writeFieldName("steps"); //$NON-NLS-1$
                generator.writeStartArray();
                List<String> steps = ObjectUtils.listSteps(preparation.getStep(), versionRepository);
                for (String step : steps) {
                    generator.writeString(step);
                }
                generator.writeEndArray();
                // Actions
                Step step = versionRepository.get(preparation.getStep().id(), Step.class);
                Blob blob = versionRepository.get(step.getContent(), Blob.class);
                String content = blob.getContent();
                // Write content to preparation
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tree = mapper.reader().readTree(content);
                Iterator<Map.Entry<String, JsonNode>> fields = tree.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> next = fields.next();
                    generator.writeFieldName(next.getKey());
                    generator.writeRawValue(next.getValue().toString());
                    if (fields.hasNext()) {
                        generator.writeRaw(","); //$NON-NLS-1$
                    }
                }
            }
        }
        generator.writeEndObject();
        generator.flush();
    }

    private PreparationRepository getRepository() {
        if (applicationContext.getBeanNamesForType(PreparationRepository.class).length > 0) {
            return applicationContext.getBean(PreparationRepository.class);
        } else {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
