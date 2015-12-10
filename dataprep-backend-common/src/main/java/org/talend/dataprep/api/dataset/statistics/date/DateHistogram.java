package org.talend.dataprep.api.dataset.statistics.date;

import org.talend.dataprep.api.dataset.statistics.Histogram;
import org.talend.dataprep.api.dataset.statistics.HistogramRange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateHistogram implements Histogram<LocalDate> {
    private static final long serialVersionUID = 1L;

    public static final String TYPE = "date";

    private final List<HistogramRange<LocalDate>> items = new ArrayList<>();

    @Override
    public List<HistogramRange<LocalDate>> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DateHistogram))
            return false;

        DateHistogram that = (DateHistogram) o;

        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
