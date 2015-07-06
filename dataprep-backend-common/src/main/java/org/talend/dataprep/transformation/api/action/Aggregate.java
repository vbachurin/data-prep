package org.talend.dataprep.transformation.api.action;

import java.util.List;
import java.util.function.BiConsumer;

public class Aggregate<T, U> implements BiConsumer<T, U> {

    private final List<BiConsumer<T, U>> actions;

    private Aggregate(List<BiConsumer<T, U>> actions) {
        this.actions = actions;
    }

    public static <T, U> BiConsumer<T, U> aggregate(List<? extends BiConsumer<T, U>> actions) {
        return new Aggregate(actions);
    }

    @Override
    public void accept(T t, U transformationContext) {
        for (BiConsumer<T, U> action : actions) {
            action.accept(t, transformationContext);
        }
    }

}
