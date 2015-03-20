package org.talend.dataprep.preparation.json;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.ColumnMetadata;
import org.talend.dataprep.api.DataSetMetadata;
import org.talend.dataprep.api.Quality;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.talend.dataprep.preparation.Blob;
import org.talend.dataprep.preparation.Preparation;
import org.talend.dataprep.preparation.Repository;
import org.talend.dataprep.preparation.Step;

@Component
class PreparationJsonSerializer extends JsonSerializer<Preparation> implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void serialize(Preparation preparation, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        // "[{\"id\":\"913ba86b06e65d9c78fcd398b4c15a28d591abfd\",\"dataSetId\":\"1234\",\"author\":null,\"creationDate\":0,\"actions\":[]}]"
        generator.writeStartObject();
        {
            generator.writeStringField("id", preparation.id());
            generator.writeStringField("dataSetId", preparation.getDataSetId());
            generator.writeStringField("author", preparation.getAuthor());
            generator.writeNumberField("creationDate", preparation.getCreationDate());
            // Actions
            Repository versionRepository = getRepository();
            if (versionRepository != null) {
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
                        generator.writeRaw(",");
                    }
                }
            }
        }
        generator.writeEndObject();
        generator.flush();
    }

    private Repository getRepository() {
        if (applicationContext.getBeanNamesForType(Repository.class).length > 0) {
            return applicationContext.getBean(Repository.class);
        } else {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
