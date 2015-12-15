package org.talend.dataprep.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Javabean that models a CSV value
 */
public class Separator {

    /** The value char. */
    private char value;

    /** Total count of separators. */
    private int totalCount = 0;

    /** Current line count. */
    private Map<Integer, Long> countPerLine = new HashMap<>();

    /**
     * Constructor.
     *
     * @param separator the value to use.
     */
    public Separator(char separator) {
        this.value = separator;
    }

    /**
     * @return the value.Math.log(frequency)
     */
    public char getSeparator() {
        return value;
    }

    /**
     * Add one to the current total count.
     */
    public void incrementCount(int lineNumber) {
        totalCount++;

        if (!countPerLine.containsKey(lineNumber)) {
            countPerLine.put(lineNumber, 0L);
        }
        countPerLine.put(lineNumber, countPerLine.get(lineNumber) + 1);
    }

    /**
     * Computes the entropy of the current separator according to the specified number of lines used as the sample size.
     * The score is based upon <a href="https://fr.wikipedia.org/wiki/Entropie_de_Shannon">Shanon entropy</a>.
     *
     * Lower is the entropy better it is, i.e, the number of occurrence of the separator remains stable from while
     * processing the lines, which means that the separator has a huge probability of being the wanted one.
     *
     * @param numberOfLines
     * @return
     */
    public double entropy(int numberOfLines) {
        HashMap<Long, Long> separatorCountOccurrences = new HashMap<>();

        if (numberOfLines <= 0) {
            throw (new IllegalArgumentException("The number of lines must be strictly positive"));
        }

        if (countPerLine.isEmpty()) {
            return Double.MAX_VALUE;
        }

        for (Long separatorCount : countPerLine.values()) {
            // increment the number of occurrence of the separatorCount
            Long separatorCountOccurrence = separatorCountOccurrences.get(separatorCount);
            separatorCountOccurrence = separatorCountOccurrence == null ? 1L : separatorCountOccurrence + 1;
            separatorCountOccurrences.put(separatorCount, separatorCountOccurrence);
        }

        double entropy = separatorCountOccurrences.values().stream().mapToDouble(s -> (double) s / numberOfLines) //
                .map(s -> s * Math.log(s)).sum();

        entropy = (-entropy * Math.log(2)) / numberOfLines;
        return entropy;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Separator{" + "value=" + value + ", totalCount=" + totalCount + "}";
    }
}
