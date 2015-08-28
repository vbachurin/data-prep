package org.talend.dataprep.transformation.aggregation.operation;

import java.util.function.BiConsumer;

import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;

/**
 * Count aggregator.
 */
public class Count extends AbstractAggregator implements Aggregator {

    /**
     * Count aggregator constructor. Package visible to ensure the use of the factory.
     * 
     * @param groupBy group by key.
     * @param columnId column id to aggregate.
     */
    Count(String groupBy, String columnId) {
        super(groupBy, columnId);
    }

    /**
     * @see BiConsumer#accept(Object, Object)
     */
    @Override
    public void accept(DataSetRow row, AggregationResult result) {
        final String key = row.get(groupBy);
        // skip value not found
        if (StringUtils.isEmpty(key)) {
            return;
        }

        // init the group by in the result
        if (!result.contains(key)) {
            result.put(key, new NumberContext(0d));
        }

        NumberContext context = (NumberContext) result.get(key);
        double count = context.getValue();
        context.setValue(count + 1);
    }
}
