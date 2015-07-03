package org.talend.dataprep.api.service.command.preparation;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.CloneInputStream;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.api.service.command.transformation.Transform;
import org.talend.dataprep.preparation.store.ContentCache;

@Component
@Scope("request")
public class PreparationGetContent extends PreparationCommand<InputStream> {

    private final String id;

    private final String version;

    private PreparationGetContent(HttpClient client, String id, String version) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
        this.version = version;
    }

    @Override
    protected InputStream run() throws Exception {
        PreparationContext preparationContext = getContext(id, version);
        List<Action> actions = preparationContext.getActions();
        InputStream content = preparationContext.getContent();
        // Run transformation (if any action to perform)
        if (!actions.isEmpty()) {
            final String encodedActions = serialize(actions);
            // pass content to the transformation service as input as well as actions to perform on input...
            final Transform transformCommand = context.getBean(Transform.class, client, content, encodedActions);
            content = transformCommand.execute();
        }
        // Cache result (if wasn't already cached)
        if (preparationContext.fromCache()) {
            return content;
        } else {
            final OutputStream newCacheEntry = contentCache.put(id, //
                    preparationContext.getVersion(), //
                    ContentCache.TimeToLive.DEFAULT);
            return new CloneInputStream(content, newCacheEntry);
        }
    }

}
