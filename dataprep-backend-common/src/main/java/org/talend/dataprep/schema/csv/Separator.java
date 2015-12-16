package org.talend.dataprep.schema.csv;

import java.util.HashMap;
import java.util.Map;

/**
 * Javabean that models a CSV value
 */
public class Separator {

    /** The value char. */
    protected char value;
    /** Total count of separators. */
    protected int totalCount = 0;
    /** Current line count. */
    protected Map<Integer, Long> countPerLine = new HashMap<>();

    /** This separator score. */
    protected double score = Double.MAX_VALUE;

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
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Separator{" + "value='" + value + '\'' + ", totalCount=" + totalCount + ", score=" + score + '}';
    }
}
