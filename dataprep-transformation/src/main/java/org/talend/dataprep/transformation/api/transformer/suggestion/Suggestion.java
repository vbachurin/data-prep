package org.talend.dataprep.transformation.api.transformer.suggestion;

import static java.util.stream.StreamSupport.stream;
import static org.talend.dataprep.transformation.api.transformer.suggestion.model.Suitability.Builder.builder;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.api.type.TypeUtils;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.suggestion.model.Delimiter;
import org.talend.dataprep.transformation.api.transformer.suggestion.model.Detail;
import org.talend.dataprep.transformation.api.transformer.suggestion.model.Suitability;
import org.talend.dataquality.statistics.quality.ValueQualityAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityStatistics;

@Component
public class Suggestion {

    private static final String[] DELIMITERS = { ":", ",", ";", "|" };

    private static final Logger LOGGER = LoggerFactory.getLogger(Suggestion.class);

    @Autowired
    ActionMetadata[] actions;

    private static final Map<String, Delimiter> initialDelimiters;

    static {
        Map<String, Delimiter> map = new HashMap<>();
        for (String delimiter : DELIMITERS) {
            map.put(delimiter, new Delimiter(delimiter));
        }
        initialDelimiters = Collections.unmodifiableMap(map);
    }

    private static double homogeneity(DataSetRow[] rows, ColumnMetadata column) {
        final List<Type> allTypes = Type.get(column.getType()).list();
        double homogeneity = 0;
        for (Type type : allTypes) {
            ValueQualityAnalyzer analyzer = new ValueQualityAnalyzer(TypeUtils.convert(type));
            for (DataSetRow row : rows) {
                analyzer.analyze(row.get(column.getId()));
            }
            final long l = analyzer.getResult().get(0).getValidCount() / rows.length;
            homogeneity += l * l;
        }
        return homogeneity;
    }

    private static double[] empty(DataSetRow[] rows, Detail[] details) {
        Type[] columnTypes = new Type[rows[0].values().size()];
        Arrays.fill(columnTypes, Type.ANY);
        ValueQualityAnalyzer analyzer = new ValueQualityAnalyzer(TypeUtils.convert(columnTypes));
        for (DataSetRow row : rows) {
            final Map<String, Object> rowValues = row.values();
            final List<String> strings = stream(rowValues.values().spliterator(), false) //
                    .map(String::valueOf) //
                    .collect(Collectors.<String> toList());
            analyzer.analyze(strings.toArray(new String[strings.size()]));
        }
        double[] emptyCount = new double[columnTypes.length];
        final List<ValueQualityStatistics> result = analyzer.getResult();
        for (int i = 0; i < result.size(); i++) {
            details[i].empty = result.get(i).getEmptyCount();
        }
        return emptyCount;
    }

    private static void delimiters(DataSetRow[] rows, Detail[] details) {
        //
        Map<String, Delimiter>[] delimiters = new HashMap[details.length];
        for (int i = 0; i < delimiters.length; i++) {
            delimiters[i] = new HashMap<>(initialDelimiters);
        }
        //
        for (DataSetRow row : rows) {
            final String[] strings = row.toArray(DataSetRow.SKIP_TDP_ID);
            for (int i = 0; i < strings.length; i++) {
                String value = strings[i];
                for (String delimiter : DELIMITERS) {
                    if (value.contains(delimiter)) {
                        delimiters[i].get(delimiter).count++;
                    }
                }
            }
        }
        //
        for (int i = 0; i < delimiters.length; i++) {
            details[i].delimiters = new ArrayList<>(delimiters[i].values());
        }
    }

    public List<ActionMetadata> suggest(DataSet dataSet) {
        List<ColumnMetadata> columns = dataSet.getColumns();
        ColumnMetadata[] allColumns = columns.toArray(new ColumnMetadata[columns.size()]);
        List<DataSetRow> collect = dataSet.getRecords().limit(50).map(DataSetRow::clone).collect(Collectors.toList());
        DataSetRow[] allRows = collect.toArray(new DataSetRow[collect.size()]);

        final Suitability score = suitability(allRows, allColumns);
        /*final String s = Visitor.PRINTER.call(score);
        System.out.println("s = " + s);*/
        if (score.getScore() == 0) {
            // Score = 0, nothing to be done on data set, no action to take on data set
            System.out.println("=> Perfect!");
            return Collections.emptyList();
        } else {
            System.out.println("=> Not (yet) perfect...");
        }

        return Collections.emptyList();
    }

    public Suitability suitability(DataSetRow[] rows, ColumnMetadata[] columns) {
        // Homogeneity
        double[] homogeneity = new double[columns.length];
        for (int i = 0; i < columns.length; i++) {
            ColumnMetadata column = columns[i];
            final double h = homogeneity(rows, column);
            LOGGER.trace("Homogeneity ({}) = {}.", column.getId(), h);
            homogeneity[i] = h;
        }
        LOGGER.trace("Homogeneity of data set = {}", homogeneity);
        // Empty + delimiters
        Detail[] details = new Detail[columns.length];
        for (int i = 0; i < details.length; i++) {
            details[i] = new Detail();
        }
        empty(rows, details);
        delimiters(rows, details);

        return builder().rows(rows.length) //
                .columns(columns.length) //
                .detail(details) //
                .build();
    }

}
