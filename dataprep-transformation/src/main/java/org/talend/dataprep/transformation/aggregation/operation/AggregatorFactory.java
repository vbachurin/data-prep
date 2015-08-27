package org.talend.dataprep.transformation.aggregation.operation;

import org.springframework.stereotype.Service;
import org.talend.dataprep.transformation.aggregation.api.AggregationOperation;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;

/**
 * Create on demand aggregator.
 */
@Service
public class AggregatorFactory {

    /**
     * Return the first aggregator out of the given parameters.
     *
     * @param parameters the aggregation parameters.
     * @return the first aggregator out of the given parameters or an empty aggregator if there's none.
     */
    public Aggregator get(AggregationParameters parameters) {

        // return empty aggregator if empty
        if (parameters.getOperations().isEmpty() || parameters.getGroupBy().isEmpty()) {
            return (k, o) -> {
            };
        }

        final AggregationOperation operation = parameters.getOperations().get(0);
        String groupBy = parameters.getGroupBy().get(0);

        switch (operation.getOperator()) {
        case COUNT:
            return new Count(groupBy, operation.getColumnId());
        case AVERAGE:
            return new Average(groupBy, operation.getColumnId());
        default:
            return (k, o) -> {
            };
        }
    }
}
