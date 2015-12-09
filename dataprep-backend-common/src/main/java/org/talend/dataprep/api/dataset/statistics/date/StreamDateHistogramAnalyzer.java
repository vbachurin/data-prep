package org.talend.dataprep.api.dataset.statistics.date;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DataType.Type;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.talend.dataprep.api.type.Type.DATE;

/**
 * Date histogram analyzer
 */
public class StreamDateHistogramAnalyzer implements Analyzer<StreamDateHistogramStatistics> {

    private static final long serialVersionUID = 1L;

    /**
     * List of statistics (one for each column)
     */
    private final ResizableList<StreamDateHistogramStatistics> stats = new ResizableList<>(StreamDateHistogramStatistics.class);

    /**
     * The columns types
     */
    private final Type[] types;

    /**
     * The columns metadata
     */
    private final List<ColumnMetadata> columns;

    /**
     * The list of date formatters, for each column, based on the list of patterns
     */
    private final Map<ColumnMetadata, List<DateTimeFormatter>> dateFormatters;

    /**
     * Constructor
     *
     * @param columns The columns metadata
     * @param types   The columns data types
     */
    public StreamDateHistogramAnalyzer(final List<ColumnMetadata> columns, final Type[] types) {
        this.columns = columns;
        this.types = types;
        this.dateFormatters = columns.stream()
                .filter(col -> DATE.isAssignableFrom(col.getType()))
                .collect(Collectors.toMap(Function.identity(), col -> {
                    final List<PatternFrequency> patterns = col.getStatistics().getPatternFrequencies();
                    return patterns.stream()
                            .map(PatternFrequency::getPattern)
                            .filter(pattern -> !pattern.isEmpty())
                            .map(pattern -> {
                                try {
                                    return DateTimeFormatter.ofPattern(pattern);
                                } catch (final Exception e) {
                                    return null;
                                }
                            })
                            .filter(formatter -> formatter != null)
                            .collect(toList());
                }));
    }

    @Override
    public boolean analyze(String... record) {
        if (record.length != types.length)
            throw new IllegalArgumentException("Each column of the record should be declared a DataType.Type corresponding! \n"
                    + types.length + " type(s) declared in this histogram analyzer but " + record.length
                    + " column(s) was found in this record. \n"
                    + "Using method: setTypes(DataType.Type[] types) to set the types. ");

        stats.resize(record.length);

        for (int index = 0; index < types.length; ++index) {
            final ColumnMetadata column = this.columns.get(index);
            final Type type = this.types[index];
            final String value = record[index];
            if (type != Type.DATE || !TypeInferenceUtils.isValid(type, value)) {
                continue;
            }

            final LocalDate adaptedValue = convertToDate(column, value);
            if (adaptedValue == null) {
                continue;
            }

            stats.get(index).add(adaptedValue);
        }

        return true;
    }

    /**
     * Convert the String value to Date
     *
     * @param column The column metadata.
     * @param value  The value to convert.
     * @return The resulting date.
     */
    private LocalDate convertToDate(final ColumnMetadata column, final String value) {
        return dateFormatters.get(column).stream()
                .map(formatter -> {
                    try {
                        return LocalDate.parse(value, formatter);
                    } catch (final DateTimeParseException e) {
                        return null;
                    }
                })
                .filter(res -> res != null)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Analyzer<StreamDateHistogramStatistics> merge(Analyzer<StreamDateHistogramStatistics> another) {
        throw new NotImplementedException();
    }

    @Override
    public void end() {
    }

    @Override
    public List<StreamDateHistogramStatistics> getResult() {
        return stats;
    }

    @Override
    public void init() {
    }

    @Override
    public void close() throws Exception {
    }
}
