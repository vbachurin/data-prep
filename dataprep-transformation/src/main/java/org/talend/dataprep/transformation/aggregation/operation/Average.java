package org.talend.dataprep.transformation.aggregation.operation;

import java.util.function.BiConsumer;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.aggregation.api.WorkingContext;

/**
 * Average aggregator.
 */
public class Average extends AbstractAggregator implements Aggregator {

    /**
     * Count aggregator constructor. Package visible to ensure the use of the factory.
     * 
     * @param groupBy group by key.
     * @param columnId column id to aggregate.
     */
    Average(String groupBy, String columnId) {
        super(groupBy, columnId);
    }

    /**
     * @see BiConsumer#accept(Object, Object)
     */
    @Override
    public void accept(DataSetRow row, AggregationResult result) {
        final String key = row.get(groupBy);

        // init the group by in the result
        if (!result.contains(key)) {
            result.put(key, new AverageContext());
        }

        final AverageContext context = (AverageContext) result.get(key);
        context.process(row.get(columnId));
    }

    /**
     * Working context for average.
     */
    class AverageContext implements WorkingContext {

        /** How many occurrences. */
        private long count;

        /** Sum of the occurrences. */
        private double sum;

        /**
         * Constructor that initialize the context.
         */
        AverageContext() {
            this.count = 0;
            this.sum = 0;
        }

        /**
         * Update the context with the given value.
         * 
         * @param value the row value to process.
         */
        void process(String value) {
            // skip null value
            if (value == null) {
                return;
            }

            double newAmount;
            try {
                newAmount = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // skip non number
                return;
            }

            sum = +newAmount;
            count++;
        }

        /**
         * @see WorkingContext#getValue()
         */
        @Override
        public double getValue() {
            return sum / count;
        }

    }
}
