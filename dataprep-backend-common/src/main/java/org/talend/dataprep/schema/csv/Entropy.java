package org.talend.dataprep.schema.csv;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Computes the entropy of the separator according to the specified number of lines used as the sample size. The score
 * is based upon <a href="https://en.wikipedia.org/wiki/Entropy_(information_theory)">Shanon entropy</a>.
 *
 * The lower the entropy, the better, i.e, the number of occurrence of the separator remains stable while processing the
 * lines, which means that the separator has a huge probability of being the wanted one.
 */
public class Entropy implements Consumer<Separator> {

    /** Number of lines read in the dataset. */
    private int numberOfLines;

    /**
     * Constructor.
     * 
     * @param numberOfLines the number of lines.
     */
    public Entropy(int numberOfLines) {
        if (numberOfLines <= 0) {
            throw new IllegalArgumentException("The number of lines must be strictly positive");
        }
        this.numberOfLines = numberOfLines;
    }

    /**
     * @see Consumer#accept(Object)
     */
    @Override
    public void accept(Separator separator) {
        HashMap<Long, Long> separatorCountOccurrences = new HashMap<>();

        if (separator.countPerLine.isEmpty()) {
            separator.score = Double.MAX_VALUE;
            return;
        }

        for (Long separatorCount : separator.countPerLine.values()) {
            // increment the number of occurrence of the separatorCount
            Long separatorCountOccurrence = separatorCountOccurrences.get(separatorCount);
            separatorCountOccurrence = separatorCountOccurrence == null ? 1L : separatorCountOccurrence + 1;
            separatorCountOccurrences.put(separatorCount, separatorCountOccurrence);
        }

        double entropy = separatorCountOccurrences.values().stream().mapToDouble(s -> (double) s / numberOfLines) //
                .map(s -> s * Math.log(s)).sum();

        entropy = (-entropy * Math.log(2)) / numberOfLines;
        separator.score = entropy;
    }
}
