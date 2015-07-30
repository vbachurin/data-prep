package org.talend.dataprep.api.preparation.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.*;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class PreparationJsonSerializer extends JsonSerializer<Preparation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreparationJsonSerializer.class);

    @Autowired(required = false)
    PreparationRepository versionRepository;

    @Autowired(required = false)
    ActionMetadata[] actionMetadata;

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
            if (versionRepository != null && preparation.getStep() != null) {
                // Steps
                final List<String> steps = PreparationUtils.listSteps(preparation.getStep(), versionRepository);
                generator.writeObjectField("steps", steps); //$NON-NLS-1$

                // Actions
                Step step = versionRepository.get(preparation.getStep().id(), Step.class);
                PreparationActions prepActions = versionRepository.get(step.getContent(), PreparationActions.class);
                List<Action> actions = prepActions.getActions();
                generator.writeObjectField("actions", actions); //$NON-NLS-1$

                // Actions metadata
                if (actionMetadata != null) {
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
                } else {
                    LOGGER.debug("No action metadata available, unable to serialize action metadata for preparation {}.",
                            preparation.id());
                }
            } else {
                LOGGER.debug("No version repository available, unable to serialize steps for preparation {}.", preparation.id());
            }
        }
        generator.writeEndObject();
        generator.flush();
    }
}
