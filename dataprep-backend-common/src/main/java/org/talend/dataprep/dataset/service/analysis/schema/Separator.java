package org.talend.dataprep.dataset.service.analysis.schema;

public class Separator {

    public char separator;

    int totalCount = 0;

    int totalOfSquaredCount = 0;

    int currentLineCount = 0;

    double averagePerLine;

    double stddev;
}
