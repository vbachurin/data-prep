package org.talend.dataprep.transformation.format;

import java.util.Collections;

import org.springframework.stereotype.Component;

/**
 * XLS format type.
 */
@Component("format#" + XlsFormat.XLS)
public class XlsFormat extends ExportFormat {

    /** XLS format type name. */
    public static final String XLS = "XLS";

    /**
     * Default constructor.
     */
    public XlsFormat() {
        super(XLS, "application/vnd.ms-excel", ".xls", false, true, Collections.<Parameter> emptyList());
    }
}
