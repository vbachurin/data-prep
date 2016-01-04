package org.talend.dataprep.schema.csv;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Computes the entropy of the separator according to the specified number of lines used as the sample size. The score
 * is based upon <a href="https://en.wikipedia.org/wiki/Entropy_(information_theory)">Shanon entropy</a>.
 *
 * The lower the entropy, the better, i.e, the number of occurrence of the separator remains stable while processing the
 * lines, which means that the separator has a huge probability of being the wanted one.
 */
public class SeparatorAnalyzer implements Consumer<Separator> {

    /** Number of lines read in the dataset. */
    private int numberOfLines;

    /**
     * Some sample lines used to make the score more accurate.
     */
    private List<String> sampleLines;

    /**
     * Constructor.
     *
     * @param numberOfLines the number of lines.
     */
    public SeparatorAnalyzer(int numberOfLines, List<String> sampleLines) {
        if (numberOfLines <= 0) {
            throw new IllegalArgumentException("The number of lines must be strictly positive");
        }
        this.numberOfLines = numberOfLines;
        this.sampleLines = sampleLines;
    }

    /**
     * Returns the frequency of each count (per line) of the separator.
     * 
     * @param separator
     * @return
     */
    private Collection<Double> countFrequency(Separator separator) {
        HashMap<Long, Long> countOccurrences = new HashMap<>();

        if (separator.countPerLine.isEmpty()) {
            separator.score = Double.MAX_VALUE;
            return Collections.emptyList();
        }
        // count the number of occurrence of each count
        for (Long separatorCount : separator.countPerLine.values()) {
            // increment the number of occurrence of the separatorCount
            Long separatorCountOccurrence = countOccurrences.get(separatorCount);
            separatorCountOccurrence = separatorCountOccurrence == null ? 1L : separatorCountOccurrence + 1;
            countOccurrences.put(separatorCount, separatorCountOccurrence);
        }
        int zeroCount = numberOfLines - separator.countPerLine.size();
        if (zeroCount > 0) {
            countOccurrences.put(0L, (long) zeroCount);
        }
        return countOccurrences.values().stream().mapToDouble(s -> (double) s / numberOfLines).boxed()
                .collect(Collectors.toList());
    }

    /**
     * @see Consumer#accept(Object)
     */
    @Override
    public void accept(Separator separator) {
        final Collection<Double> countFrequencies = countFrequency(separator);
        separator.score = entropy(countFrequencies);
    }

    /**
     *
     * @param collection
     * @return
     */
    public double entropy(Collection<Double> collection) {
        if (collection.isEmpty()) {
            return Double.MAX_VALUE;
        }
        double entropy = collection.stream().mapToDouble(d -> d).map(s -> s * Math.log(s)).sum();
        entropy = (-entropy * Math.log(2));
        return entropy;
    }
}