package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.talend.dataprep.api.type.Type.DATE;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractDate extends AbstractActionMetadata {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDate.class);

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return ActionCategory.DATE.getDisplayName();
    }

    /**
     * Only works on 'date' columns.
     *
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        final String domain = column.getDomain().toUpperCase();
        return DATE.equals(Type.get(column.getType())) || SemanticCategoryEnum.DATE.name().equals(domain);
    }

    protected LocalDateTime superParse(String value, DataSetRow row, String columnId) throws DateTimeException {
        return superParse(value, getPatterns(row, columnId));
    }

    /**
     * Almost like DateTimeFormatter.parse(), but tries all the DateTimeFormatter given as parameters. The result is
     * returned once the first matching pattern is found.
     *
     * @param value the text to parse
     * @return the parsed date-time
     * @throws DateTimeException if none of the formats can match text
     */
    protected LocalDateTime superParse(String value, List<DatePattern> patterns) throws DateTimeException {
        // take care of the null value
        if (value == null) {
            throw new DateTimeException("cannot parse null");
        }

        for (DatePattern pattern : patterns) {
            final DateTimeFormatter formatter = pattern.getFormatter();

            // first try to parse directly as LocalDateTime
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeException e) {
                // if it fails, let's try the LocalDate first
                try {
                    LocalDate temp = LocalDate.parse(value, formatter);
                    return temp.atStartOfDay();
                } catch (DateTimeException e2) {
                    // nothing to do here, just try the next formatter
                }
            }
        }
        throw new DateTimeException("Test [" + value + "] does not match any known pattern");
    }

    /**
     * Utility method to read all of the presents date pattern in this column, looking in the DQ stats.
     *
     * @param row the row to get the patterns from.
     */
    protected List<DatePattern> getPatterns(DataSetRow row, String columnId) {
        final ColumnMetadata column = row.getRowMetadata().getById(columnId);

        // parse and checks the new date pattern
        final JsonFactory jsonFactory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        // store the current pattern in the context
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final JsonNode patternFrequencyTable = rootNode.get("patternFrequencyTable"); //$NON-NLS-1$

        List<DatePattern> patterns = new ArrayList<>();

        for (int i = 0; i < patternFrequencyTable.size(); i++) {
            String pattern = patternFrequencyTable.get(i).get("pattern").asText(); //$NON-NLS-1$
            // skip empty patterns
            if (StringUtils.isEmpty(pattern)) {
                continue;
            }
            // skip existing patterns
            if (contains(pattern, patterns)) {
                continue;
            }
            final JsonNode occurrencesNode = patternFrequencyTable.get(i).get("occurrences"); //$NON-NLS-1$
            Integer occurrences = 0;
            if (occurrencesNode != null) {
                occurrences = occurrencesNode.asInt();
            }
            patterns.add(new DatePattern(occurrences, pattern));
        }

        Collections.sort(patterns);
        return computeDateTimeFormatter(patterns);
    }

    /**
     * Return true if the given pattern is already held in the list of patterns.
     *
     * @param pattern the pattern to check.
     * @param patterns the list of patterns.
     * @return true if the given pattern is already held in the list of patterns.
     */
    private boolean contains(String pattern, List<DatePattern> patterns) {
        return patterns.stream().anyMatch(p -> StringUtils.equals(pattern, p.getPattern()));
    }

    /**
     * Giving a list of potential pattern as strings, validate them, and compute a list of DateTimeFormatter.
     *
     * @param patterns the list of potential patterns
     * @return a list that contains only valid and non null, non empty DateTimeFormatter
     */
    protected List<DatePattern> computeDateTimeFormatter(List<DatePattern> patterns) {

        DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();

        final Iterator<DatePattern> iterator = patterns.iterator();
        while (iterator.hasNext()) {
            final DatePattern nextPattern = iterator.next();
            String pattern = nextPattern.getPattern();
            // remove empty patterns
            if (StringUtils.isEmpty(pattern)) {
                iterator.remove();
                continue;
            }
            try {
                dtfb.appendPattern(pattern);
                nextPattern.setFormatter(DateTimeFormatter.ofPattern(pattern));
            } catch (IllegalArgumentException e) {
                // remove invalid patterns
                iterator.remove();
            }
        }

        return patterns;
    }

}
