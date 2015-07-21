package org.talend.dataprep.transformation.api.action;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A aggregator for multiple {@link BiConsumer consumers}. May be removed if a standard replacement is found.
 *
 * @param <T> the type of the first argument to the function.
 * @param <U> the type of the second argument to the function.
 * @param <R> the type of the result of the function.
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
     * @param <T> the type of the first argument to the function.
     * @param <U> the type of the second argument to the function.
     * @param <R> the type of the result of the function.
     * @return A unique {@link BiConsumer} that wraps all provided consumers in parameter.
     */
    public static <T, U, R> BiFunction<T, U, R> aggregate(List<? extends BiFunction<T, U, R>> functions) {
        return new AggregateFunctions(functions);
    }

    /**
     * @see BiFunction#apply(Object, Object)
     */
    @Override
    public R apply(T t, U u) {
        final List<R> collect = functions.stream() //
                .filter(action -> action != null) //
                .map(action -> action.apply(t, u)) //
                .collect(Collectors.toList());
        // TODO is there any better solution than returning null when there's no action to perform ?
        if (collect.isEmpty()) {
            return null;
        }
        return collect.get(collect.size() - 1);
    }
}
