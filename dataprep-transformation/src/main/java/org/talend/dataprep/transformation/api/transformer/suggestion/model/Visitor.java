package org.talend.dataprep.transformation.api.transformer.suggestion.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Visitor<R> {

    public static final Visitor<String> PRINTER = new Visitor<String>() {

        {
            when(Suitability.class, (s, v) -> {
                StringBuilder builder = new StringBuilder();
                builder.append("Score: ").append(s.getScore());
                builder.append("Details:\n");
                for (Detail detail : s.getDetails()) {
                    builder.append(v.call(detail));
                }
                return builder.toString();
            });
            when(Detail.class, (d, v) -> {
                StringBuilder builder = new StringBuilder();
                builder.append("\tEmpty: ").append(d.getEmpty());
                builder.append("\tHomogeneity: ").append(d.getHomogeneity());
                builder.append("\tDelimiters:\n");
                for (Delimiter delimiter : d.getDelimiters()) {
                    builder.append(v.call(delimiter));
                }
                return builder.toString();
            });
            when(Delimiter.class, (d, v) -> "\t\tDelimiter: " + d.getDelimiter() +
                    "\n\t\tCount: " + d.getCount() + "\n");
        }
    };
    private final Map<Class<?>, BiFunction<Object, Visitor<R>, ? extends R>> map = new HashMap<>();

    public <T> Visitor<R> when(Class<? extends T> type, BiFunction<? super T, Visitor<R>, ? extends R> fun) {
        map.put(type, (BiFunction<Object, Visitor<R>, ? extends R>) fun);
        return this;
    }

    public R call(Object receiver) {
        return map.getOrDefault(receiver.getClass(), (obj, visitor) -> {
            throw new IllegalArgumentException("Unsupported type " + obj.getClass().getName() + ".");
        }).apply(receiver, this);
    }
}