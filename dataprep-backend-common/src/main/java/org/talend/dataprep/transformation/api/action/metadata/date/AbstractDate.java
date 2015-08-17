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
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

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
     * returned once the first matching pattenr is found.
     * 
     * @param value the text to parse
     * @param formats list of DateTimeFormatter to try
     * @return the parsed date-time
     * @throws DateTimeException if none of the formats can match text
     */
    protected TemporalAccessor superParse(String value, List<DateTimeFormatter> formats) throws DateTimeException {
        for (DateTimeFormatter formatToTest : formats) {
            try {
                return formatToTest.parse(value);
            } catch (DateTimeException e) {
                // Nothing to do, just try value against next pattern
            }
        }

        throw new DateTimeException("Test [" + value + "] does not match any known pattern");
    }

    /**
     * Utility method to read all of the presents date pattern in this column, looking in the DQ stats.
     */
    protected List<DateTimeFormatter> readPatternFromJson(DataSetRow row, String columnId) {
        final ColumnMetadata column = row.getRowMetadata().getById(columnId);

        // parse and checks the new date pattern
        final JsonFactory jsonFactory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        // store the current pattern in the context
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final JsonNode mostUsedPatternNode = rootNode.get("patternFrequencyTable").get(0); //$NON-NLS-1$

        List<DateTimeFormatter> toReturn = new ArrayList<>();
        for (int i = 0; i < rootNode.get("patternFrequencyTable").size(); i++) {
            String pattern = rootNode.get("patternFrequencyTable").get(i).get("pattern").asText();
            toReturn.add(DateTimeFormatter.ofPattern(pattern));
        }
        return toReturn;
    }

}
