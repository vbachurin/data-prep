package org.talend.dataprep.api.dataset.statistics.date;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataquality.statistics.frequency.pattern.DateTimePatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.PatternFrequencyStatistics;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DataType.Type;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
     * A date pattern analyzer
     */
    private final DateTimePatternFrequencyAnalyzer dateTimePatternFrequencyAnalyzer;

    /**
     * Constructor
     * @param types   The columns data types
     * @param dateTimePatternFrequencyAnalyzer
     */
    public StreamDateHistogramAnalyzer(final Type[] types, final DateTimePatternFrequencyAnalyzer dateTimePatternFrequencyAnalyzer) {
        this.types = types;
        this.dateTimePatternFrequencyAnalyzer = dateTimePatternFrequencyAnalyzer;
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
            final Type type = this.types[index];
            final String value = record[index];
            if (type != Type.DATE || !TypeInferenceUtils.isValid(type, value)) {
                continue;
            }

            dateTimePatternFrequencyAnalyzer.analyze(record);
            final PatternFrequencyStatistics frequencyPatterns = dateTimePatternFrequencyAnalyzer.getResult().get(index);
            final LocalDate adaptedValue = convertToDate(frequencyPatterns, value);
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
     * @param patternFrequencyStatistics The date pattern statistics.
     * @param value  The value to convert.
     * @return The resulting date.
     */
    private LocalDate convertToDate(final PatternFrequencyStatistics patternFrequencyStatistics, final String value) {
        return patternFrequencyStatistics.getTopK(20).keySet().stream()
                .map(pattern -> {
                    try {
                        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                        return LocalDate.parse(value, formatter);
                    } catch (final Exception e) {
                        return null;
                    }
                })
                .filter(date -> date != null)
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
