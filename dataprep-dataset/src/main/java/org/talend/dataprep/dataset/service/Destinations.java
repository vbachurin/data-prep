package org.talend.dataprep.dataset.service;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

public interface Destinations {

    /**
     * JMS Destination for schema analysis of a data set.
     * 
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    String SCHEMA_ANALYSIS = "org.talend.tdp.dataset.content.schema"; //$NON-NLS-1

    /**
     * JMS Destination for content indexing of a data set.
     * 
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    String CONTENT_ANALYSIS = "org.talend.tdp.dataset.content.index"; //$NON-NLS-1

    /**
     * JMS Destination for data statistics information of a data set.
     * 
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    String STATISTICS_ANALYSIS = "org.talend.tdp.dataset.content.statistics"; //$NON-NLS-1

    /**
     * JMS Destination for data quality information of a data set.
     *
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    String QUALITY_ANALYSIS = "org.talend.tdp.dataset.content.quality"; //$NON-NLS-1


    /**
     * JMS Destination for format analysis of a data set.
     *
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, InputStream, HttpServletResponse)
     */
    String FORMAT_ANALYSIS = "org.talend.tdp.dataset.content.format"; //$NON-NLS-1$
}
