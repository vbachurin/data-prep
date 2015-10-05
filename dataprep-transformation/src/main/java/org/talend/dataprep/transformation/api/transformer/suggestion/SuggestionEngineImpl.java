package org.talend.dataprep.transformation.api.transformer.suggestion;

import static org.talend.dataprep.transformation.api.transformer.suggestion.model.Suitability.Builder.builder;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.suggestion.model.*;

@Component
public class SuggestionEngineImpl implements SuggestionEngine {

    private static final String[] DELIMITERS = { ":", ",", ";", "|" };

    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestionEngineImpl.class);

    private static final Map<String, Delimiter> initialDelimiters;

    static {
        Map<String, Delimiter> map = new HashMap<>();
        for (String delimiter : DELIMITERS) {
            map.put(delimiter, new Delimiter(delimiter));
        }
        initialDelimiters = Collections.unmodifiableMap(map);
    }

    @Autowired
    ActionMetadata[] actions;

    private static double[] empty(ColumnMetadata[] columns, Detail[] details) {
        double[] emptyCount = new double[columns.length];
        for (int i = 0; i < columns.length; i++) {
            details[i].empty = columns[i].getStatistics().getEmpty();
        }
        return emptyCount;
    }

    private static void delimiters(ColumnMetadata[] columns, Detail[] details) {
        //
        Map<String, Delimiter>[] delimiters = new HashMap[details.length];
        for (int i = 0; i < delimiters.length; i++) {
            delimiters[i] = new HashMap<>(initialDelimiters);
        }
        //
        for (int i = 0; i < columns.length; i++) {
            final ColumnMetadata column = columns[i];
            final List<PatternFrequency> patterns = column.getStatistics().getPatternFrequencies();
            for (PatternFrequency pattern : patterns) {
                for (String delimiter : DELIMITERS) {
                    if (pattern.getPattern().contains(delimiter)) {
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
        try {
            final Suitability score = suitability(allColumns);
            if (score.getScore() == 0) {
                // Score = 0, nothing to be done on data set, no action to take on data set
                System.out.println("=> Perfect!");
                return Collections.emptyList();
            } else {
                System.out.println("=> Not (yet) perfect... (score: " + score.getScore() + ").");
                System.out.println("details => " + Visitor.PRINTER.call(score));
            }
            return Collections.emptyList();
        } finally {
            dataSet.getRecords().close();
        }
    }

    public Suitability suitability(ColumnMetadata[] columns) {
        // Homogeneity
        double[] homogeneity = new double[columns.length];
        for (int i = 0; i < columns.length; i++) {
            ColumnMetadata column = columns[i];
            final long l = column.getStatistics().getValid() / column.getStatistics().getCount();
            final double h = (double) (l * l);
            LOGGER.trace("Homogeneity ({}) = {}.", column.getId(), h);
            homogeneity[i] = h;
        }
        LOGGER.trace("Homogeneity of data set = {}", homogeneity);
        // Empty + delimiters
        Detail[] details = new Detail[columns.length];
        for (int i = 0; i < details.length; i++) {
            details[i] = new Detail();
        }
        empty(columns, details);
        delimiters(columns, details);

        return builder().rows((int) columns[0].getStatistics().getCount()) //
                .columns(columns.length) //
                .detail(details) //
                .build();
    }

    @Override
    public List<Suggestion> score(List<ActionMetadata> actions, ColumnMetadata column) {
        return null;
    }
}
