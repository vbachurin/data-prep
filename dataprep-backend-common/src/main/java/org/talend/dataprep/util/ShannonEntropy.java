package org.talend.dataprep.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Computes the <a href="https://en.wikipedia.org/wiki/Entropy_(information_theory)">Shannon entropy</a>.
 */
public class ShannonEntropy {

    /**
     * Computes the Shannon entropy according to the frequencies of values specified in <tt>frequencies</tt>.
     * 
     * @param frequencies the collection containing the frequencies of values
     * @return the shannon compute
     */
    public static double computeWithFrequencies(Collection<Double> frequencies) {
        if (frequencies.contains(0D)) {
            throw new IllegalArgumentException("Values with null frequency are not permitted when computing Shannon entropy");
        }

        if (frequencies.isEmpty()) {
            return Double.MAX_VALUE;
        }
        double entropy = frequencies.stream().mapToDouble(d -> d).map(s -> s * Math.log(s)).sum();
        entropy = (-entropy / Math.log(2));

        return entropy;
    }

    /**
     * Computes the Shannon entropy of values specified in <tt>values</tt>.
     * 
     * @param values the list of occurring values
     * @param <T> the types of values specified in <tt>values</tt>
     * @return
     */
    public static <T> double computeWithValues(Collection<T> values) {
        final Map<T, Integer> map = new HashMap<T, Integer>();
        // count the occurrences of each value
        for (T t : values) {
            if (!map.containsKey(t)) {
                map.put(t, 0);
            }
            map.put(t, map.get(t) + 1);
        }

        Collection<Double> frequencies = map.values().stream().mapToDouble(s -> (double) s / values.size()).boxed()
                .collect(Collectors.toList());
        return computeWithFrequencies(frequencies);
    }

}
