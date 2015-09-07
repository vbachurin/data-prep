package org.talend.dataprep.api.dataset.statistics;

import java.util.LinkedList;
import java.util.List;

public class Histogram {

    List<HistogramRange> ranges = new LinkedList<>();

    public List<HistogramRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<HistogramRange> ranges) {
        this.ranges = ranges;
    }
}
