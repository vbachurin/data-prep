package org.talend.dataprep.api.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SimpleFilterService implements FilterService {

    @Override
    public Predicate<DataSetRow> build(String filterAsString) {
        if (filterAsString == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        if (StringUtils.EMPTY.equals(filterAsString)) {
            return r -> true;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            final JsonNode tree = mapper.reader().readTree(filterAsString);
            final Iterator<JsonNode> elements = tree.elements();
            if (!elements.hasNext()) {
                throw new IllegalArgumentException("Malformed filter: " + filterAsString);
            } else {
                final JsonNode node = elements.next();
                final String columnName = node.get("field").asText();
                final String value = node.get("value").asText();
                if (tree.has("eq")) {
                    return r -> StringUtils.equals(r.get(columnName), value);
                } else if (tree.has("gt")) {
                    return safe(r -> Integer.parseInt(r.get(columnName)) > Integer.parseInt(value));
                } else if (tree.has("lt")) {
                    return safe(r -> Integer.parseInt(r.get(columnName)) < Integer.parseInt(value));
                } else if (tree.has("gte")) {
                    return safe(r -> Integer.parseInt(r.get(columnName)) >= Integer.parseInt(value));
                } else if (tree.has("lte")) {
                    return safe(r -> Integer.parseInt(r.get(columnName)) <= Integer.parseInt(value));
                } else {
                    throw new UnsupportedOperationException("Not supported: " + tree.asText());
                }
            }
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_FILTER, e);
        }
    }

    private static Predicate<DataSetRow> safe(Predicate<DataSetRow> inner) {
        return r -> {
            try {
                return inner.test(r);
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
}
