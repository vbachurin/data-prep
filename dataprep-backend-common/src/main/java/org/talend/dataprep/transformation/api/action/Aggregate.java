package org.talend.dataprep.transformation.api.action;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * A aggregator for multiple {@link BiConsumer consumers}. May be removed if a standard replacement is found.
 * 
 * @param <T>
 * @param <U>
 */
public class Aggregate<T, U> implements BiConsumer<T, U> {

    /** All aggregated actions */
    private final List<BiConsumer<T, U>> actions;

    /**
     * Create a {@link BiConsumer} instance that groups all consumers in parameters.
     * 
     * @param actions {@link BiConsumer Consumers} to be aggregated.
     * @see #aggregate(List)
     */
    private Aggregate(List<BiConsumer<T, U>> actions) {
        this.actions = actions;
    }

    /**
     * Aggregate all provided actions into one {@link BiConsumer} implementation.
     * 
     * @param actions {@link BiConsumer Consumers} to be aggregated.
     * @param <T>
     * @param <U>
     * @return A unique {@link BiConsumer} that wraps all provided consumers in parameter.
     */
    public static <T, U> BiConsumer<T, U> aggregate(List<? extends BiConsumer<T, U>> actions) {
        return new Aggregate(actions);
    }

    /**
     * @see BiConsumer#accept(Object, Object)
     */
    @Override
    public void accept(T t, U u) {
        for (BiConsumer<T, U> action : actions) {
            action.accept(t, u);
        }
    }

}
