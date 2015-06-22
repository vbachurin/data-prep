package org.talend.dataprep.transformation.api.transformer.exporter;

import org.talend.dataprep.api.type.ExportType;

/**
 * Nothing really important just a marker class to have the list of export formats
 */
public interface Exporter {

    /**
     * @return One of the value of available {@link ExportType}, should not return <code>null</code>.
     */
    ExportType getExportType();

}
