package org.talend.dataprep.api.dataset.statistics.date;

import org.talend.dataprep.api.dataset.statistics.HistogramRange;

import java.time.LocalDate;

/**
 * Histogram item where the range is a date range
 */
public class DateHistogramRange extends HistogramRange<LocalDate> {

    private static final long serialVersionUID = 1L;

    public static final String TYPE = "date";
}
