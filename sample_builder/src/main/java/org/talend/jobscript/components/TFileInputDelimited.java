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

import org.talend.StringsUtils;
import org.talend.jobscript.Column;

public class TFileInputDelimited extends AbstractComponent {

    private String filePath;

    public TFileInputDelimited(String filePath, Column... columns) {
        super("tFileInputDelimited", "tFileInputDelimited_1", columns);
        this.filePath = filePath;
    }

    @Override
    public String generate(int position) {
        String toReturn = "addComponent {" + "\n";

        toReturn += getComponentDefinition(position);

        toReturn += "setSettings {" + "\n";
        toReturn += "FILENAME : \"\\\"" + filePath + "\\\"\"," + "\n";

        toReturn += "CSV_OPTION : \"false\"," + "\n";
        toReturn += "ROWSEPARATOR : \"\\\"\\\\n\\\"\"," + "\n";
        toReturn += "CSVROWSEPARATOR : \"\\\"\\\\n\\\"\"," + "\n";
        toReturn += "FIELDSEPARATOR : \"\\\";\\\"\"," + "\n";
        toReturn += "ESCAPE_CHAR : \"\\\"\\\"\\\"\"," + "\n";
        toReturn += "TEXT_ENCLOSURE : \"\\\"\\\"\\\"\"," + "\n";
        toReturn += "HEADER : \"1\"," + "\n";
        toReturn += "FOOTER : \"0\"," + "\n";
        toReturn += "REMOVE_EMPTY_ROW : \"false\"," + "\n";
        toReturn += "UNCOMPRESS : \"false\"," + "\n";
        toReturn += "DIE_ON_ERROR : \"false\"," + "\n";
        toReturn += "ADVANCED_SEPARATOR : \"false\"," + "\n";
        toReturn += "THOUSANDS_SEPARATOR : \"\\\",\\\"\"," + "\n";
        toReturn += "DECIMAL_SEPARATOR : \"\\\".\\\"\"," + "\n";
        toReturn += "RANDOM : \"false\"," + "\n";
        toReturn += "NB_RANDOM : \"10\"," + "\n";
        toReturn += "TRIMALL : \"false\"," + "\n";

        toReturn += "TRIMSELECT {" + "\n";
        for (Column column : inputSchema) {
            toReturn += "   SCHEMA_COLUMN : \"" + column.name + "\"," + "\n";
            toReturn += "   TRIM : \"false\",\n";
        }
        toReturn = StringsUtils.removeLastChars(toReturn, 2);
        toReturn += "\n}," + "\n";

        toReturn += "CHECK_FIELDS_NUM : \"false\"," + "\n";
        toReturn += "CHECK_DATE : \"false\"," + "\n";
        toReturn += "ENCODING : \"\\\"US-ASCII\\\"\"," + "\n";
        toReturn += "ENCODING:ENCODING_TYPE : \"CUSTOM\"," + "\n";
        toReturn += "SPLITRECORD : \"false\"," + "\n";
        toReturn += "ENABLE_DECODE : \"false\"," + "\n";

        toReturn += "DECODE_COLS {" + "\n";
        for (Column column : inputSchema) {
            toReturn += "   SCHEMA_COLUMN : \"" + column.name + "\"," + "\n";
            toReturn += "   DECODE : \"false\",\n";
        }
        toReturn = StringsUtils.removeLastChars(toReturn, 2);
        toReturn += "\n}," + "\n";

        toReturn += "SCHEMA_OPT_NUM : \"100\"," + "\n";
        toReturn += "LABEL : \"states\"," + "\n";
        toReturn += "CONNECTION_FORMAT : \"row\"" + "\n";

        toReturn += "}" + "\n";

        toReturn = addSchema(toReturn);

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
