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

public class TLogRow extends AbstractComponent {

    public TLogRow() {
        super("tLogRow", "tLogRow_1");
    }

    @Override
    public String generate(int position) {
        String toReturn = "addComponent {" + "\n";

        toReturn += getComponentDefinition(position);

        toReturn += "setSettings {" + "\n";
        toReturn += "BASIC_MODE: \"false\"," + "\n";
        toReturn += "TABLE_PRINT: \"true\"," + "\n";
        toReturn += "VERTICAL: \"false\"," + "\n";
        toReturn += "PRINT_UNIQUE: \"true\"," + "\n";
        toReturn += "PRINT_LABEL: \"false\"," + "\n";
        toReturn += "PRINT_UNIQUE_LABEL: \"false\"," + "\n";
        toReturn += "FIELDSEPARATOR: \"\\\"|\\\"\"," + "\n";
        toReturn += "PRINT_HEADER: \"false\"," + "\n";
        toReturn += "PRINT_UNIQUE_NAME: \"false\"," + "\n";
        toReturn += "PRINT_COLNAMES: \"false\"," + "\n";
        toReturn += "USE_FIXED_LENGTH: \"false\"," + "\n";

        toReturn += "LENGTHS{" + "\n";
        for (Column column : inputSchema) {
            toReturn += "   SCHEMA_COLUMN : \"" + column.name + "\"," + "\n";
            toReturn += "   LENGTH : \"10\",\n";
        }
        toReturn = StringsUtils.removeLastChars(toReturn, 2);
        toReturn += "\n}," + "\n";
        toReturn += "SCHEMA_OPT_NUM: \"100\"," + "\n";
        toReturn += "CONNECTION_FORMAT: \"row\"" + "\n";
        toReturn += "}" + "\n";

        toReturn = addSchema(toReturn);

        toReturn += "}" + "\n";

        return toReturn;
    }

}
