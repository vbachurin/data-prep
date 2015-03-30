package org.talend.dataprep.transformation.api.action.metadata;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

@Component(Negate.ACTION_BEAN_PREFIX + Negate.NEGATE_ACTION_NAME)
public class Negate extends SingleColumnAction {

    public static final Log            LOGGER             = LogFactory.getLog(Negate.class);

    public static final String         NEGATE_ACTION_NAME = "negate";                       //$NON-NLS-1$

    private Negate() {
    }

    @Override
    public String getName() {
        return NEGATE_ACTION_NAME;
    }

    @Override
    public String getCategory() {
        return "boolean"; //$NON-NLS-1$
    }

    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    @Override
    @Nonnull
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_NAME_PARAMETER };
    }

    @Override
    public Consumer<DataSetRow> create(Map<String, String> parsedParameters) {
        return row -> {
            String columnName = parsedParameters.get(COLUMN_NAME_PARAMETER_NAME);
            String value = row.get(columnName);

            if (value != null && (value.trim().equalsIgnoreCase("true") || value.trim().equalsIgnoreCase("false"))) { //$NON-NLS-1$//$NON-NLS-2$
                Boolean boolValue = Boolean.valueOf(value);
                row.set(columnName, toProperCase("" + !boolValue)); //$NON-NLS-1$
            }
        };
    }

    // TODO move this
    protected static String toProperCase(String from) {
        StringReader in = new StringReader(from.toLowerCase());
        boolean precededBySpace = true;
        StringBuilder properCase = new StringBuilder();
        while (true) {
            try {
                int i = in.read();
                if (i == -1) {
                    break;
                }
                char c = (char) i;
                if (c == ' ' || c == '"' || c == '(' || c == '.' || c == '/' || c == '\\' || c == ',') {
                    properCase.append(c);
                    precededBySpace = true;
                } else {
                    if (precededBySpace) {
                        properCase.append(Character.toUpperCase(c));
                    } else {
                        properCase.append(c);
                    }
                    precededBySpace = false;
                }
            } catch (IOException e) {
                // should not occurs when reading a String
                LOGGER.warn("This error should not occurs", e);
            }
        }

        return properCase.toString();
    }

    @Override
    public Set<Type> getCompatibleColumnTypes() {
        return Collections.singleton(Type.BOOLEAN);
    }

}
