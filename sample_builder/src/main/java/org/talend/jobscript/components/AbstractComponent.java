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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.talend.jobscript.Column;

public abstract class AbstractComponent {

	protected String componentType;

	protected String componentName;

	protected List<Column> inputSchema = new ArrayList<>();

	protected List<Column> outputSchema = new ArrayList<>();

	public AbstractComponent(String componentType, String componentName,
			Column... columns) {
		super();
		this.componentType = componentType;
		this.componentName = componentName;
		this.setInputSchema(Arrays.asList(columns));
	}

	public abstract String generate();

	protected String getComponentDefinition() {
		String toReturn = "setComponentDefinition {" + "\n";
		toReturn += "TYPE: \"" + componentType + "\"," + "\n";
		toReturn += "NAME: \"" + componentName + "\"," + "\n";
		toReturn += "POSITION: 96, 64," + "\n";
		toReturn += "SIZE: 32, 32," + "\n";
		toReturn += "OFFSETLABEL: 0, 0" + "\n";
		toReturn += "}" + "\n";
		return toReturn;
	}

	protected String addSchema(String toReturn) {
		toReturn += "addSchema {" + "\n";
		toReturn += "NAME: \"" + componentName + "\"," + "\n";
		toReturn += "TYPE: \"FLOW\"" + "\n";
		for (Column column : outputSchema) {
			toReturn += "addColumn {" + "\n";
			toReturn += "NAME: \""
					+ column.name
					+ "\",\n TYPE: \""
					+ column.type
					+ "\",\nNULLABLE: true,\nCOMMENT: \"\",\nPATTERN: \"\\\"dd-MM-yyyy\\\"\",\nSOURCETYPE: \"\""
					+ "\n";
			toReturn += "}" + "\n";
		}
		toReturn += "}" + "\n";
		return toReturn;
	}

	public String getComponentType() {
		return componentType;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setInputSchema(List<Column> columns) {
		this.inputSchema = columns;
		this.outputSchema = columns;
	}

	public List<Column> getInputSchema() {
		return inputSchema;
	}

	public List<Column> getOutputSchema() {
		return outputSchema;
	}

}
