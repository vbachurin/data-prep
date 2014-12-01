// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.jobscript.components;

import java.util.List;

import org.talend.StringsUtils;
import org.talend.jobscript.Column;

public class TFileInputExcel extends AbstractComponent {

    private String       filePath;

    private String       sheetname;

    public TFileInputExcel(String filePath, String sheetname,
			Column... columns) {
        super("tFileInputExcel", "tFileInputExcel_1",columns);
        this.filePath = filePath;
        this.sheetname = sheetname;
    }

    @Override
    public String generate() {
        String toReturn = "addComponent {" + "\n";

        toReturn += getComponentDefinition();

        toReturn += "setSettings {" + "\n";
        toReturn += "VERSION_2007: \"false\"," + "\n";
        toReturn += "FILENAME : \"\\\"" + filePath + "\\\"\"," + "\n";
        toReturn += "ALL_SHEETS: \"false\"," + "\n";
        toReturn += "SHEETLIST {\nSHEETNAME : \"\\\"" + sheetname + "\\\"\",\n USE_REGEX : \"false\"\n}," + "\n";
        toReturn += "HEADER: \"1\"," + "\n";
        toReturn += "FOOTER: \"0\"," + "\n";
        toReturn += "AFFECT_EACH_SHEET: \"false\"," + "\n";
        toReturn += "FIRST_COLUMN: \"1\"," + "\n";
        toReturn += "DIE_ON_ERROR: \"false\"," + "\n";
        toReturn += "ADVANCED_SEPARATOR: \"false\"," + "\n";
        toReturn += "THOUSANDS_SEPARATOR: \"\\\",\\\"\"," + "\n";
        toReturn += "DECIMAL_SEPARATOR: \"\\\".\\\"\"," + "\n";
        toReturn += "TRIMALL: \"false\"," + "\n";
        toReturn += "TRIMSELECT {" + "\n";
        for (Column column : inputSchema) {
            toReturn += "   SCHEMA_COLUMN : \"" + column.name + "\"," + "\n";
            toReturn += "   TRIM : \"false\",\n";
        }
        toReturn = StringsUtils.removeLastChars(toReturn, 2);
        toReturn += "\n}," + "\n";
        toReturn += "CONVERTDATETOSTRING : \"false\"," + "\n";
        toReturn += "DATESELECT {" + "\n";
        for (Column column : inputSchema) {
            toReturn += "   SCHEMA_COLUMN : \"" + column.name + "\"," + "\n";
            toReturn += "   CONVERTDATE : \"false\"," + "\n";
            toReturn += "   PATTERN : \"\\\"MM-dd-yyyy\\\"\",\n";
        }
        toReturn = StringsUtils.removeLastChars(toReturn, 2);
        toReturn += "\n}," + "\n";
        toReturn += "ENCODING : \"\\\"ISO-8859-15\\\"\"," + "\n";
        toReturn += "ENCODING:ENCODING_TYPE : \"ISO-8859-15\"," + "\n";
        toReturn += "READ_REAL_VALUE : \"false\"," + "\n";
        toReturn += "STOPREAD_ON_EMPTYROW : \"false\"," + "\n";
        toReturn += "NOVALIDATE_ON_CELL : \"false\"," + "\n";
        toReturn += "SUPPRESS_WARN : \"false\"," + "\n";
        toReturn += "GENERATION_MODE : \"USER_MODE\"," + "\n";
        toReturn += "CONNECTION_FORMAT : \"row\"" + "\n";
        toReturn += "}" + "\n";

        toReturn = addSchema( toReturn);

        toReturn += "addSchema {" + "\n";
        toReturn += "NAME: \"REJECT\"," + "\n";
        toReturn += "TYPE: \"REJECT\"" + "\n";
        for (Column column : inputSchema) {
            toReturn += "addColumn {" + "\n";
            toReturn += "NAME: \"" + column.name + "\",\n TYPE: \"" + column.type
                    + "\",\nNULLABLE: true,\nCOMMENT: \"\",\nPATTERN: \"\\\"dd/MM/yyyy\\\"\"\n";
            toReturn += "}" + "\n";
        }
        toReturn += "addColumn {" + "\n";
        toReturn += "NAME: \"errorCode\",\n TYPE: \"id_String\",\nNULLABLE: true,\nDEFAULTVALUE: \"\",\nLENGTH: 255,\n SOURCETYPE: \"\"\n";
        toReturn += "}" + "\n";
        toReturn += "addColumn {" + "\n";
        toReturn += "NAME: \"errorMessage\",\n TYPE: \"id_String\",\nNULLABLE: true,\nDEFAULTVALUE: \"\",\nLENGTH: 255,\n SOURCETYPE: \"\"\n";
        toReturn += "}" + "\n";
        toReturn += "}" + "\n";

        toReturn += "}" + "\n";

        return toReturn;
    }

}
