package org.talend.dataprep.transformation.api.transformer.suggestion.model;

public class Delimiter {

    private final String delimiter;

    public double count;

    public Delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public double getCount() {
        return count;
    }
}
