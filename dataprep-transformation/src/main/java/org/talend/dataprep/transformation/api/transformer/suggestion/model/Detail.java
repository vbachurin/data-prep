package org.talend.dataprep.transformation.api.transformer.suggestion.model;

import java.util.LinkedList;
import java.util.List;

public class Detail {

    public double empty;

    double homogeneity;

    public List<Delimiter> delimiters = new LinkedList<>();

    public double getEmpty() {
        return empty;
    }

    public double getHomogeneity() {
        return homogeneity;
    }

    public List<Delimiter> getDelimiters() {
        return delimiters;
    }

}
