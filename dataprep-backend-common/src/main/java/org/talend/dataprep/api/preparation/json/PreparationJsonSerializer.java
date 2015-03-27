package org.talend.dataprep.api.preparation.json;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
class PreparationJsonSerializer extends JsonSerializer<Preparation> implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void serialize(Preparation preparation, JsonGenerator generator, SerializerProvider serializerProvider)
            throws IOException {
        generator.writeStartObject();
        {
            generator.writeStringField("id", preparation.id()); //$NON-NLS-1$
            generator.writeStringField("dataSetId", preparation.getDataSetId()); //$NON-NLS-1$
            generator.writeStringField("author", preparation.getAuthor()); //$NON-NLS-1$
            generator.writeStringField("name", preparation.getName()); //$NON-NLS-1$
            generator.writeNumberField("creationDate", preparation.getCreationDate()); //$NON-NLS-1$
            PreparationRepository versionRepository = getRepository();
            if (versionRepository != null && preparation.getStep() != null) {
                // Steps
                final List<String> steps = PreparationUtils.listSteps(preparation.getStep(), versionRepository);
                generator.writeObjectField("steps", steps);

                // Actions
                Step step = versionRepository.get(preparation.getStep().id(), Step.class);
                PreparationActions prepActions = versionRepository.get(step.getContent(), PreparationActions.class);
                generator.writeObjectField("actions", prepActions.getActions());
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
