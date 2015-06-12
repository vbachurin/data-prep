package org.talend.dataprep.schema;

/**
 * Javabean that models a CSV separator
 */
public class Separator {

    /** The separator char. */
    private char separator;

    /** Total count of separators. */
    private int totalCount;

    /** Average per line. */
    private double averagePerLine;

    /**
     * Constructor.
     * 
     * @param separator the separator to use.
     */
    public Separator(char separator) {
        this.separator = separator;
        totalCount = 0;
    }

    /**
     * @return the separator.
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * @return the total count.
     */
    int getTotalCount() {
        return totalCount;
    }

    /**
     * Add one to the current total count.
     */
    void totalCountPlusOne() {
        totalCount++;
    }

    /**
     * @return the average per line.
     */
    double getAveragePerLine() {
        return averagePerLine;
    }

    /**
     * @param averagePerLine the average per line to set.
     */
    void setAveragePerLine(double averagePerLine) {
        this.averagePerLine = averagePerLine;
    }

}
