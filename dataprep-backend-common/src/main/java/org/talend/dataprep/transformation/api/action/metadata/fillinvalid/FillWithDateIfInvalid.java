package org.talend.dataprep.transformation.api.action.metadata.fillinvalid;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(value = FillWithDateIfInvalid.ACTION_BEAN_PREFIX + FillWithDateIfInvalid.FILL_INVALID_ACTION_NAME)
public class FillWithDateIfInvalid extends AbstractFillIfInvalid {

    private static final Logger LOGGER = LoggerFactory.getLogger(FillWithDateIfInvalid.class);

    public static final String FILL_INVALID_ACTION_NAME = "fillinvalidwithdefaultdate"; //$NON-NLS-1$

    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final String DEFAULT_DATE_VALUE = DEFAULT_FORMATTER.format( LocalDateTime.of( 1970, Month.JANUARY, 1, 10, 0 ) );

    @Override
    public String getName() {
        return FILL_INVALID_ACTION_NAME;
    }

    @Override
    @Nonnull
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter(DEFAULT_VALUE_PARAMETER, Type.DATE.getName(), DEFAULT_DATE_VALUE));
        return parameters;
    }

    @Override
    public void applyOnColumn(DataSetRow row, TransformationContext context, Map<String, String> parameters, String columnId) {
        final String value = row.get(columnId);

        final RowMetadata rowMetadata = row.getRowMetadata();
        final ColumnMetadata column = rowMetadata.getById(columnId);
        if (column == null) {
            return;
        }

        try {
            final Set<String> invalidValues = column.getQuality().getInvalidValues();
            if (StringUtils.isEmpty(value) || invalidValues.contains(value)) {
                // we assume all controls have been made in the ui.
                String newDateStr = parameters.get(DEFAULT_VALUE_PARAMETER);

                // we search the most used pattern
                String mostUsedPattern = findMostUsedDatePattern(column);

                String newDateWithFormat = DateTimeFormatter.ofPattern(mostUsedPattern) //
                        .format(LocalDateTime.parse(newDateStr, DEFAULT_FORMATTER));

                row.set(columnId, newDateWithFormat);
            }
        } catch (Exception e) {
            LOGGER.warn("skip error parsing date", e);
        }
    }

    protected String findMostUsedDatePattern(final ColumnMetadata column) {

        // get the date pattern to write the date with
        final JsonFactory jsonFactory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        // register the new pattern in column stats, to be able to process date action later
        final JsonNode rootNode = getStatisticsNode(mapper, column);
        final JsonNode patternFrequencyTable = rootNode.get("patternFrequencyTable"); //$NON-NLS-1$

        int maxOccurence = 0, maxOccurenceIdx = 0;

        for (int i = 0, size = patternFrequencyTable.size(); i < size; i++) {
            int occurrences = patternFrequencyTable.get(i).get("occurrences").asInt();
            if (occurrences > maxOccurence) {
                maxOccurenceIdx = i;
                maxOccurence = occurrences;
            }
        }

        String mostUsedPattern = patternFrequencyTable.get(maxOccurenceIdx).get("pattern").asText();

        return mostUsedPattern;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        Type type = Type.get(column.getType());
        return Type.DATE.equals(type);
    }

    public boolean isDate() {
        return true;
    }

}
