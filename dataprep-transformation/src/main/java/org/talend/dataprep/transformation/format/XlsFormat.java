package org.talend.dataprep.transformation.format;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;
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
    //@formatter:off
    public XlsFormat() {
        super(XLS, "application/vnd.ms-excel", ".xls", true, true,
                Collections.singletonList(
                        new Parameter( Parameter.FILENAME_PARAMETER, //
                            "EXPORT_FILENAME",  //
                            "text", //
                            new ParameterValue( StringUtils.EMPTY, "EXPORT_FILENAME_DEFAULT" ), //
                            Collections.emptyList() //
                )));
    }
    //@formatter:on
}
