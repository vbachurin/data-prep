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

import org.talend.jobscript.Column;


public abstract class InputComponent {

    protected String componentType;

    protected String componentName;

    public InputComponent(String componentType, String componentName) {
        super();
        this.componentType = componentType;
        this.componentName = componentName;
    }

    public abstract String generate(List<Column> columns);

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

    public String getComponentType() {
        return componentType;
    }

    public String getComponentName() {
        return componentName;
    }

}
