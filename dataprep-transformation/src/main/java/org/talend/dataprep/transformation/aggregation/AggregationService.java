package org.talend.dataprep.transformation.aggregation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.aggregation.operation.Aggregator;
import org.talend.dataprep.transformation.aggregation.operation.AggregatorFactory;

/**
 * Service in charge of... aggregation !
 */
@Service
public class AggregationService {

    /** Aggregator factory. */
    @Autowired
    private AggregatorFactory factory;

    /**
     * Process an aggregation.
     *
     * @param parameters the aggregation parameters.
     * @param dataset the dataset input.
     * @return the aggregation result.
     */
    public AggregationResult aggregate(AggregationParameters parameters, DataSet dataset) {

        // check the parameters
        if (parameters.getOperations().isEmpty() || parameters.getGroupBy().isEmpty()) {
            throw new TDPException(CommonErrorCodes.BAD_AGGREGATION_PARAMETERS);
        }

        AggregationResult result = new AggregationResult(parameters.getOperations().get(0).getOperator());

        // get the aggregator
        Aggregator aggregator = factory.get(parameters);

        // process the dataset
        dataset.getRecords().forEach(row -> aggregator.accept(row, result));

        return result;
    }
}
