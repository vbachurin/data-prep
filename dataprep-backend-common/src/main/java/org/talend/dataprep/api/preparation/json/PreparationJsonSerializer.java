package org.talend.dataprep.api.preparation.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.*;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;

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
            generator.writeNumberField("lastModificationDate", preparation.getLastModificationDate()); //$NON-NLS-1$
            PreparationRepository versionRepository = getRepository();
            if (versionRepository != null && preparation.getStep() != null) {
                // Steps
                final List<String> steps = PreparationUtils.listSteps(preparation.getStep(), versionRepository);
                generator.writeObjectField("steps", steps);

                // Actions
                Step step = versionRepository.get(preparation.getStep().id(), Step.class);
                PreparationActions prepActions = versionRepository.get(step.getContent(), PreparationActions.class);
                List<Action> actions = prepActions.getActions();
                generator.writeObjectField("actions", actions); //$NON-NLS-1$
                // Actions metadata
                Collection<ActionMetadata> actionMetadata = getActionMetadata();
                List<ActionMetadata> metadataList = new ArrayList<>(actions.size());
                for (Action action : actions) {
                    String actionName = action.getAction();
                    for (ActionMetadata metadata : actionMetadata) {
                        if (metadata.getName().equals(actionName)) {
                            metadataList.add(metadata);
                            break;
                        }
                    }
                }
                generator.writeObjectField("metadata", metadataList); //$NON-NLS-1$
            }
        }
        generator.writeEndObject();
        generator.flush();
    }

    private Collection<ActionMetadata> getActionMetadata() {
        if (applicationContext.getBeanNamesForType(PreparationRepository.class).length > 0) {
            return applicationContext.getBeansOfType(ActionMetadata.class).values();
        } else {
            return Collections.emptyList();
        }
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
