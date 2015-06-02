package org.talend.dataprep.dataset.service;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

public class Destinations {

    /**
     * JMS Destination for schema analysis of a data set.
     * 
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    public static final String SCHEMA_ANALYSIS = "org.talend.tdp.dataset.content.schema"; //$NON-NLS-1

    /**
     * JMS Destination for content indexing of a data set.
     * 
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    public static final String CONTENT_ANALYSIS = "org.talend.tdp.dataset.content.index"; //$NON-NLS-1

    /**
     * JMS Destination for data statistics information of a data set.
     * 
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    public static final String STATISTICS_ANALYSIS = "org.talend.tdp.dataset.content.statistics"; //$NON-NLS-1

    /**
     * JMS Destination for data quality information of a data set.
     *
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    public static final String QUALITY_ANALYSIS = "org.talend.tdp.dataset.content.quality"; //$NON-NLS-1

    /**
     * JMS Destination for format analysis of a data set.
     *
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    public static final String FORMAT_ANALYSIS = "org.talend.tdp.dataset.content.format"; //$NON-NLS-1$

    /**
     * Private constructor.
     */
    private Destinations() {
        // utility class should not have a public constructor.
    }
}
