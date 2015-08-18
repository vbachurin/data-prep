package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.talend.dataprep.api.type.Type.DATE;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractDate extends AbstractActionMetadata {

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

    /**
     * Almost like DateTimeFormatter.parse(), but tries all the DateTimeFormatter given as parameters. The result is
     * returned once the first matching pattern is found.
     * 
     * @param value the text to parse
     * @return the parsed date-time
     * @throws DateTimeException if none of the formats can match text
     */
    protected TemporalAccessor superParse(String value, Set<DateTimeFormatter> formatters) throws DateTimeException {
        for (DateTimeFormatter formatToTest : formatters) {
            try {
                return formatToTest.parse(value);
            } catch (DateTimeException e) {
                // Nothing to do, just try value against next pattern
            }
        }

        throw new DateTimeException("Test [" + value + "] does not match any known pattern");
    }

    protected TemporalAccessor superParse(String value, DataSetRow row, String columnId) throws DateTimeException {
        return superParse(value, computePatterns(row, columnId));
    }

    /**
     * Utility method to read all of the presents date pattern in this column, looking in the DQ stats.
     */
    protected Set<DateTimeFormatter> computePatterns(DataSetRow row, String columnId) {
        final ColumnMetadata column = row.getRowMetadata().getById(columnId);

        // parse and checks the new date pattern
        final JsonFactory jsonFactory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        // store the current pattern in the context
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final JsonNode patternFrequencyTable = rootNode.get("patternFrequencyTable");

        List<String> toReturn = new ArrayList<>();

        for (int i = 0; i < patternFrequencyTable.size(); i++) {
            String pattern = patternFrequencyTable.get(i).get("pattern").asText();

            toReturn.add(pattern);
        }
        return computePatterns(toReturn);
    }

    /**
     * Giving a list of potential pattern as strings, validate them, and compute a list of DateTimeFormatter.
     * 
     * @param patterns the list of potential patterns
     * @return a list that contains only valid and non null, non empty DateTimeFormatter
     */
    protected Set<DateTimeFormatter> computePatterns(List<String> patterns) {
        Set<DateTimeFormatter> computedFormatters = new HashSet<>();
        DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();

        // We will store here valid patterns as String, to avoid duplication, as a workaround of no equals() implemented
        // in DateTimeFormatter
        List<String> validPatterns = new ArrayList<>();

        for (String pattern : patterns) {
            if (pattern != null && pattern.length() > 1 && !validPatterns.contains(pattern)) {
                try {
                    dtfb.appendPattern(pattern);
                    validPatterns.add(pattern);
                    computedFormatters.add(DateTimeFormatter.ofPattern(pattern));
                } catch (IllegalArgumentException e) {
                    // Nothing to do, this pattern is invalid
                }
            }
        }

        return computedFormatters;
    }

}
