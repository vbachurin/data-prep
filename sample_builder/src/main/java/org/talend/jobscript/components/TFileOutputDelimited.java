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

public class TFileOutputDelimited extends AbstractComponent {

	private String filePath;

	private boolean includeHeader = true;

	public TFileOutputDelimited(String filePath, boolean includeHeader) {
		super("tFileOutputDelimited", "tFileOutputDelimited_1");
		this.filePath = filePath;
		this.includeHeader = includeHeader;
	}

	@Override
	public String generate(int position) {
		String toReturn = "addComponent {" + "\n";

		toReturn += getComponentDefinition(position);

		toReturn += "setSettings {" + "\n";
		toReturn += "USESTREAM : \"false\"," + "\n";
		toReturn += "STREAMNAME : \"outputStream\"," + "\n";
		toReturn += "FILENAME : \"\\\"" + filePath + "\\\"\"," + "\n";
		toReturn += "ROWSEPARATOR : \"\\\"\\\\n\\\"\"," + "\n";
		toReturn += "OS_LINE_SEPARATOR_AS_ROW_SEPARATOR : \"true\"," + "\n";
		toReturn += "CSVROWSEPARATOR : \"\\\"\\\\n\\\"\"," + "\n";
		toReturn += "FIELDSEPARATOR : \"\\\";\\\"\"," + "\n";
		toReturn += "APPEND : \"false\"," + "\n";
		toReturn += "INCLUDEHEADER : \"" + includeHeader + "\"," + "\n";
		toReturn += "COMPRESS : \"false\"," + "\n";
		toReturn += "ADVANCED_SEPARATOR : \"false\"," + "\n";
		toReturn += "THOUSANDS_SEPARATOR : \"\\\",\\\"\"," + "\n";
		toReturn += "DECIMAL_SEPARATOR : \"\\\".\\\"\"," + "\n";
		toReturn += "CSV_OPTION : \"false\"," + "\n";
		toReturn += "ESCAPE_CHAR : \"\\\"\\\"\\\"\"," + "\n";
		toReturn += "TEXT_ENCLOSURE : \"\\\"\\\"\\\"\"," + "\n";
		toReturn += "CREATE : \"true\"," + "\n";
		toReturn += "SPLIT : \"false\"," + "\n";
		toReturn += "SPLIT_EVERY : \"1000\"," + "\n";
		toReturn += "FLUSHONROW : \"false\"," + "\n";
		toReturn += "FLUSHONROW_NUM : \"1\"," + "\n";
		toReturn += "ROW_MODE : \"false\"," + "\n";
		toReturn += "ENCODING : \"\\\"ISO-8859-15\\\"\"," + "\n";
		toReturn += "ENCODING:ENCODING_TYPE : \"ISO-8859-15\"," + "\n";
		toReturn += "DELETE_EMPTYFILE : \"false\"," + "\n";
		toReturn += "SCHEMA_OPT_NUM : \"90\"," + "\n";
		toReturn += "CONNECTION_FORMAT : \"row\"" + "\n";
		toReturn += "}" + "\n";

		toReturn = addSchema(toReturn);

		toReturn += "}" + "\n";

		return toReturn;
	}

}
