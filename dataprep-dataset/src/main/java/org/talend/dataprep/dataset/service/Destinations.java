package org.talend.dataprep.dataset.service;

public interface Destinations {

    /**
     * JMS Destination for schema analysis of a data set.
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, java.io.InputStream)
     */
    String SCHEMA_ANALYSIS = "org.talend.tdp.dataset.content.schema"; //$NON-NLS-1

    /**
     * JMS Destination for content indexing of a data set.
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, java.io.InputStream)
     */
    String CONTENT_ANALYSIS = "org.talend.tdp.dataset.content.index"; //$NON-NLS-1

    /**
     * JMS Destination for data quality information of a data set.
     * @see org.talend.dataprep.dataset.service.DataSetService#create(String, java.io.InputStream)
     */
    String QUALITY_ANALYSIS = "org.talend.tdp.dataset.content.quality"; //$NON-NLS-1
}
