package org.talend.dataprep.transformation.api.action.metadata;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.parameters.SelectParameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility class for action metadata unit tests.
 */
public class ActionMetadataTestUtils {

    /**
     *
     * @param type the wanted column type.
     * @return a new column that matches the given type.
     */
    public static ColumnMetadata getColumn(Type type) {
        return ColumnMetadata.Builder.column().id(0).name("name").type(type).build();
    }

    /**
     * Parse the given input stream into a parameter map.
     *
     * @param action the action to parse the parameters for.
     * @param input the parameters input stream.
     * @return the parsed parameters.
     * @throws IOException if an error occurs.
     */
    public static Map<String, String> parseParameters(ActionMetadata action, InputStream input) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
        JsonNode node = mapper.readTree(input);
        return parseParameters(node.get("actions").get(0).get("parameters").fields(), action);
    }

    /**
     * Set the statistics to the given column on the given row.
     *
     * @param row the row to update.
     * @param columnId the column id.
     * @param statisticsContent the statistics in json as expected from the DQ library.
     * @throws IOException you never know :)
     */
    public static void setStatistics(DataSetRow row, String columnId, InputStream statisticsContent) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Statistics statistics = mapper.readValue(statisticsContent, Statistics.class);
        row.getRowMetadata().getById(columnId).setStatistics(statistics);
    }

    /**
     * Return a new dataset row with the given values.
     *
     * @param values the row values.
     * @return a new dataset row that have the given values.
     */
    public static DataSetRow getRow(String... values) {
        DecimalFormat format = new DecimalFormat("0000");
        Map<String, String> rowValues = new HashMap<>();
        int i = 0;
        for (String value : values) {
            rowValues.put(format.format(i), value);
            i++;
        }
        return new DataSetRow(rowValues);
    }

    /**
     * Parse the given json parameter into a map<key, value>.
     *
     * @param parameters the json parameters.
     * @param actionMetadata the action metadata.
     * @return the action parameters as a map<key, value>.
     */
    private static Map<String, String> parseParameters(Iterator<Map.Entry<String, JsonNode>> parameters,
                                                      ActionMetadata actionMetadata) {
        // get parameters ids
        final List<String> paramIds = actionMetadata.getParameters()
                .stream() //
                .map(Parameter::getName) //
                .collect(toList()); //

        // add ids from select parameters
        actionMetadata.getParameters().stream() //
                .filter(p -> p instanceof SelectParameter) //
                .flatMap(p -> ((List<SelectParameter.Item>) p.getConfiguration().get("values")).stream()) //extract select values
                .flatMap(items -> items.getInlineParameters().stream()) //extract select values inner parameters
                .forEach(inlineParam -> paramIds.add(inlineParam.getName()));

        final Map<String, String> parsedParameters = new HashMap<>();
        while (parameters.hasNext()) {
            Map.Entry<String, JsonNode> currentParameter = parameters.next();

            if (paramIds.contains(currentParameter.getKey())) {
                parsedParameters.put(currentParameter.getKey(), currentParameter.getValue().asText());
            }
        }
        return parsedParameters;
    }
}
