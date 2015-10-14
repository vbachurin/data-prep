package org.talend.dataprep.transformation.format;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.stereotype.Component;

/**
 * CSV format type.
 */
@Component("format#" + CSVFormat.CSV)
public class CSVFormat extends ExportFormat {

    /** CSV format type name. */
    public static final String CSV = "CSV";

    /**
     * Default constructor.
     */
    public CSVFormat() {
        super(CSV, "text/csv", ".csv", true, false,
                Collections
                        .singletonList(new Parameter("csvSeparator", "CHOOSE_SEPARATOR", "radio",
                                new ParameterValue(";", "SEPARATOR_SEMI_COLON"),
                                Arrays.asList(new ParameterValue("\u0009", "SEPARATOR_TAB"), // &#09;
                                        new ParameterValue(" ", "SEPARATOR_SPACE"),
                                        new ParameterValue(",", "SEPARATOR_COMMA")))));
    }
}
