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
import org.talend.dataprep.preparation.store.ContentCacheKey;

import com.netflix.hystrix.HystrixCommand;

/**
 * Command used to retrieve the preparation content.
 */
@Component
@Scope("request")
public class PreparationGetContent extends PreparationCommand<InputStream> {

    /** The preparation id. */
    private final String id;

    /** The preparation version. */
    private final String version;

    /** Optional sample size (if null or <=0, the full preparation content is returned). */
    private Long sample;

    /**
     * Constructor.
     *
     * @param client the http client to use.
     * @param id the preparation id.
     * @param version the preparation version.
     */
    private PreparationGetContent(HttpClient client, String id, String version) {
        super(APIService.PREPARATION_GROUP, client);
        this.id = id;
        this.version = version;
        this.sample = null;
    }

    /**
     * Constructor with sample size specified.
     *
     * @param client the http client to use.
     * @param id the preparation id.
     * @param version the preparation version.
     */
    private PreparationGetContent(HttpClient client, String id, String version, Long sample) {
        this(client, id, version);
        this.sample = sample;
    }

    /**
     * @see HystrixCommand#run()
     */
    @Override
    protected InputStream run() throws Exception {
        PreparationContext preparationContext = getContext(id, version, sample);
        List<Action> actions = preparationContext.getActions();
        InputStream content = preparationContext.getContent();
        // Run transformation (if any action to perform)
        if (!actions.isEmpty()) {
            final String encodedActions = serializeActions(actions);
            // pass content to the transformation service as input as well as actions to perform on input...
            final Transform transformCommand = context.getBean(Transform.class, client, content, encodedActions);
            content = transformCommand.execute();
        }
        // Cache result (if wasn't already cached)
        if (preparationContext.fromCache()) {
            return content;
        } else {
            ContentCacheKey key = new ContentCacheKey(id, preparationContext.getVersion());
            final OutputStream newCacheEntry = contentCache.put(key, ContentCache.TimeToLive.DEFAULT);
            return new CloneInputStream(content, newCacheEntry);
        }
    }

}
