// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.dataset.service.analysis.asynchronous;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.talend.dataprep.dataset.service.Destinations;
import org.talend.dataprep.dataset.service.analysis.DataSetAnalyzer;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.security.SecurityProxy;

/**
 * Compute statistics analysis on the full dataset.
 */
@Component
@ConditionalOnProperty(name = "dataset.asynchronous.analysis", havingValue = "true", matchIfMissing = true)
public class AsyncBackgroundAnalysis implements AsynchronousDataSetAnalyzer {

    @Autowired
    private BackgroundAnalysis backgroundAnalysis;

    @Autowired
    private SecurityProxy securityProxy;

    /**
     * Receives jms message to start a quality analysis.
     * 
     * @param message the jms message that holds the dataset id.
     */
    @JmsListener(destination = Destinations.STATISTICS_ANALYSIS)
    public void analyzeQuality(Message message) {
        try {
            String dataSetId = message.getStringProperty("dataset.id");
            String securityToken = message.getStringProperty("security.token");


            try {
                securityProxy.borrowIdentity(securityToken);
                analyze(dataSetId);
            } finally {
                message.acknowledge();
                securityProxy.releaseIdentity();
            }
        } catch (JMSException e) {
            throw new TDPException(DataSetErrorCodes.UNEXPECTED_JMS_EXCEPTION, e);
        }
    }

    /**
     * @see DataSetAnalyzer#analyze
     */
    @Override
    public void analyze(String dataSetId) {
        backgroundAnalysis.analyze(dataSetId);
    }

    /**
     * @see AsynchronousDataSetAnalyzer#destination()
     */
    @Override
    public String destination() {
        return Destinations.STATISTICS_ANALYSIS;
    }
}
