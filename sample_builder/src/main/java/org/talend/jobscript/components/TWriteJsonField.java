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

import java.util.Arrays;
import java.util.List;

import org.talend.StringsUtils;
import org.talend.jobscript.Column;

public class TWriteJsonField extends AbstractComponent {

	public TWriteJsonField() {
		super("tWriteJSONField", "tWriteJSONField_1");
	}

	@Override
	public void setInputSchema(List<Column> columns) {
		super.setInputSchema(columns);
		this.outputSchema = Arrays.asList(new Column("content", "id_String"));
	}

	@Override
	public String generate() {
		String toReturn = "addComponent {" + "\n";

		toReturn += getComponentDefinition();

		toReturn += "setSettings {" + "\n";
		toReturn += "JSONFIELD: \"content\"," + "\n";

		toReturn += "ROOT{" + "\n";
		toReturn += "   PATH : \"/rootTag\"," + "\n";
		toReturn += "   COLUMN : \"\",\n";
		toReturn += "   VALUE : \"\",\n";
		toReturn += "   ATTRIBUTE : \"main\",\n";
		toReturn += "   ORDER : \"1\",\n";
		int order = 2;
		for (Column column : inputSchema) {
			if (order > 2) {
				toReturn += "   PATH : \"/rootTag/" + column.name + "\","
						+ "\n";
				toReturn += "   COLUMN : \"" + column.name + "\",\n";
				toReturn += "   VALUE : \"\",\n";
				toReturn += "   ATTRIBUTE : \"branch\",\n";
				toReturn += "   ORDER : \"" + order + "\",\n";
			}
			order++;
		}
		toReturn = StringsUtils.removeLastChars(toReturn, 2);
		toReturn += "\n}," + "\n";

		toReturn += "LOOP{" + "\n";
		toReturn += "   PATH : \"/rootTag/" + inputSchema.get(0).name + "\","
				+ "\n";
		toReturn += "   COLUMN : \"" + inputSchema.get(0).name + "\",\n";
		toReturn += "   VALUE : \"\",\n";
		toReturn += "   ATTRIBUTE : \"main\",\n";
		toReturn += "   ORDER : \"2\"\n";
		toReturn += "\n}," + "\n";

		toReturn += "REMOVE_ROOT: \"true\"," + "\n";
		toReturn += "COMPACT_FORMAT: \"true\"," + "\n";
		toReturn += "CONNECTION_FORMAT: \"row\"" + "\n";
		toReturn += "}" + "\n";

		toReturn = addSchema(toReturn);

		toReturn += "}" + "\n";

		return toReturn;
	}

}
