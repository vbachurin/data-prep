package org.talend.dataprep.api.dataset.statistics.date;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.transformation.api.action.metadata.date.DateParser;
import org.talend.dataquality.statistics.frequency.pattern.DateTimePatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.PatternFrequencyStatistics;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DataType.Type;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * The columns metadata
     */
    private List<ColumnMetadata> columns;

    /**
     * The columns types
     */
    private final Type[] types;

    /**
     * A date parser, based on columns and analyzer
     */
    private final DateParser dateParser;

    /**
     * Constructor
     * @param columns The columns metadata
     * @param types   The columns data types
     * @param dateParser A date parser based on column metadata and DQ analyzer
     */
    public StreamDateHistogramAnalyzer(List<ColumnMetadata> columns, final Type[] types, final DateParser dateParser) {
        this.columns = columns;
        this.types = types;
        this.dateParser = dateParser;
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
            final ColumnMetadata column = this.columns.get(index);
            final String value = record[index];
            if (type != Type.DATE || !TypeInferenceUtils.isValid(type, value)) {
                continue;
            }

            try {
                final LocalDateTime adaptedValue = dateParser.parse(value, column);
                stats.get(index).add(adaptedValue);
            }
            catch(DateTimeException e) {
                //just skip this value
            }
        }

        return true;
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