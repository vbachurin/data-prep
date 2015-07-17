package org.talend.dataprep.transformation.api.action;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A aggregator for multiple {@link BiConsumer consumers}. May be removed if a standard replacement is found.
 * 
 * @param <T>
 * @param <U>
 */
public class AggregateFunctions<T, U, R> implements BiFunction<T, U, R> {

    /** All aggregated functions */
    private final List<BiFunction<T, U, R>> functions;

    /**
     * Create a {@link BiFunction} instance that groups all functions in parameters.
     *
     * @param actions {@link BiFunction Functions} to be aggregated.
     * @see #aggregate(List)
     */
    private AggregateFunctions(List<BiFunction<T, U, R>> actions) {
        this.functions = actions;
    }

    /**
     * Aggregate all provided functions into one {@link BiConsumer} implementation.
     * 
     * @param functions {@link BiConsumer Consumers} to be aggregated.
     * @param <T>
     * @param <U>
     * @return A unique {@link BiConsumer} that wraps all provided consumers in parameter.
     */
    public static <T, U, R> BiFunction<T, U, R> aggregate(List<? extends BiFunction<T, U, R>> functions) {
        return new AggregateFunctions(functions);
    }

    @Override
    public R apply(T t, U u) {
        final List<R> collect = functions.stream() //
                .filter(action -> action != null) //
                .map(action -> action.apply(t, u)) //
                .collect(Collectors.toList());
        return collect.get(collect.size() - 1);
    }
}
