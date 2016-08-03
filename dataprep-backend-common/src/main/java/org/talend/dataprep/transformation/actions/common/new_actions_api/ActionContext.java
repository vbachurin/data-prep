package org.talend.dataprep.transformation.actions.common.new_actions_api;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.DataSetRow;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ActionContext {

    private static final Logger log = LoggerFactory.getLogger(ActionContext.class);

    private DatasetMetadata datasetMetadata;

    private UserSelection userSelection;

    private Map<String, Object> context = new HashMap<>();

    private ImmutableMap<String, String> parameters = ImmutableMap.of();

    private Predicate<DataSetRow> filter = r -> true;

    public DatasetMetadata getDatasetMetadata() {
        return datasetMetadata;
    }

    public UserSelection getUserSelection() {
        return userSelection;
    }

    /**
     * Return the object from the context or null.
     *
     * @param key the object key.
     * @return the object (stored in the context).
     */
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    /**
     * Return the object from the context or use the supplier to create it and cache it.
     *
     * @param key      the object key.
     * @param supplier the supplier to use to cr    eate the object in case it is not found in the context.
     * @return the object (stored in the context).
     */
    public <T> T get(String key, Supplier<T> supplier) {
        if (context.containsKey(key)) {
            return (T) context.get(key);
        }

        final T value = supplier.get();
        context.put(key, value);
        log.debug("adding {}->{} in this context {}", key, value, this);
        return value;
    }

    public ImmutableMap<String, String> getParameters() {
        return parameters;
    }

    public Predicate<DataSetRow> getFilter() {
        return filter;
    }
}
