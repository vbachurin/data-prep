package org.talend.dataprep.dataset.service;

import java.io.InputStream;

public class Destinations {

    /**
     * JMS Destination for data statistics information of a data set.
     * 
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, String, InputStream, String)
     */
    public static final String STATISTICS_ANALYSIS = "org.talend.tdp.dataset.content.statistics"; //$NON-NLS-1

    /**
     * JMS Destination for data quality information of a data set.
     *
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, String, InputStream, String)
     */
    public static final String QUALITY_ANALYSIS = "org.talend.tdp.dataset.content.quality"; //$NON-NLS-1

    /**
     * Private constructor.
     */
    private Destinations() {
        // utility class should not have a public constructor.
    }
}
