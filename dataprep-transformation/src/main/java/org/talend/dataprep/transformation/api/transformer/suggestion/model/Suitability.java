package org.talend.dataprep.transformation.api.transformer.suggestion.model;

public class Suitability {

    private final int rows;

    private final int columns;

    private final Detail[] details;

    public Suitability(int rows, int columns, Detail[] details) {
        this.rows = rows;
        this.columns = columns;
        this.details = details;
    }

    public Detail[] getDetails() {
        return details;
    }

    public double getScore() {
        double emptyTotal = 0, delimitersTotal = 0, homogeneityTotal = 0;
        for (Detail detail : details) {
            emptyTotal += detail.empty;
            homogeneityTotal += detail.homogeneity;
            for (Delimiter delimiter : detail.delimiters) {
                delimitersTotal += delimiter.count;
            }
        }
        homogeneityTotal = homogeneityTotal / columns;
        double values = (emptyTotal + delimitersTotal) / (rows * columns);
        return (1 - homogeneityTotal) + values;
    }

    public static class Builder {

        private Detail[] details;

        private int rows, columns;

        public static Builder builder() {
            return new Builder();
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder columns(int columns) {
            this.columns = columns;
            return this;
        }

        public Suitability build() {
            return new Suitability(rows, columns, details);
        }

        public Builder detail(Detail[] details) {
            this.details = details;
            return this;
        }
    }

}
