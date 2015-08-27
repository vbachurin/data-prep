package org.talend.dataprep.transformation.aggregation.operation;

import java.util.function.BiConsumer;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;

/**
 * Base interface for all aggregators.
 *
 * So far, Aggregators are statefull hence not meant to be used across multiple aggregations at the same time.
 */
public interface Aggregator extends BiConsumer<DataSetRow, AggregationResult> {

}
