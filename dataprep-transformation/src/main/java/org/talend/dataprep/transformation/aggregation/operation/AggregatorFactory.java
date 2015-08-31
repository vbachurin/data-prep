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
            throw new IllegalArgumentException("Invalid aggregation parameters");
        }

        final AggregationOperation operation = parameters.getOperations().get(0);
        String groupBy = parameters.getGroupBy().get(0);

        switch (operation.getOperator()) {
        case COUNT:
            return new Count(groupBy, operation.getColumnId());
        case AVERAGE:
            return new Average(groupBy, operation.getColumnId());
        case MIN:
            return new Min(groupBy, operation.getColumnId());
        case MAX:
            return new Max(groupBy, operation.getColumnId());
        case SUM:
            return new Sum(groupBy, operation.getColumnId());
        default:
            throw new IllegalArgumentException("Operation '" + operation.getOperator() + "' not supported");
        }
    }
}
