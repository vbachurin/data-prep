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
package org.talend.jobscript;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.talend.StringsUtils;
import org.talend.jobscript.components.InputComponent;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class JobScriptBuilder {

    private List<InputComponent> components = new ArrayList<>();

    private List<Column>         columns    = new ArrayList<>();

    private String jobScript = "";

    public boolean addComponent(InputComponent component) {
        return components.add(component);
    }

    public boolean addColumn(Column column) {
        return columns.add(column);
    }

    public void build() {
        addHeader().addContexts().addParameters()
                .addInputComponents().addConnection().addSubJob();
    }

    @Override
    public String toString() {
        return jobScript;
    }

    protected JobScriptBuilder addHeader() {
        String header = "SCRIPT_VERSION=4.2,";

        jobScript += header + "\n";
        return this;
    }

    protected JobScriptBuilder addContexts() {
        String toReturn = "DEFAULT_CONTEXT: Default," + "\n";
        toReturn += "ContextType {" + "\n";
        toReturn += "NAME: Default" + "\n";
        toReturn += "}" + "\n";

        jobScript += toReturn;
        return this;
    }

    protected JobScriptBuilder addFile(String filename) {
        URL url = this.getClass().getResource(filename);
        try {
            File file = new File(url.toURI());
            for (String currentLine : Files.readLines(file, Charsets.UTF_8)) {
                this.jobScript += currentLine + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return this;
    }

    protected JobScriptBuilder addParameters() {
        return this.addFile("jobScript_parameters.txt");
    }

    protected JobScriptBuilder addInputComponents() {
        for (InputComponent component : components) {
            this.jobScript += component.generate(columns);
        }
        return this;
    }

    protected JobScriptBuilder addConnection() {
        String toReturn = "addConnection {" + "\n";
        toReturn += "TYPE: \"FLOW\"," + "\n";
        toReturn += "NAME: \"row1\"," + "\n";
        toReturn += "METANAME: \"" + components.get(0).getComponentName() + "\"," + "\n";
        toReturn += "SOURCE: \"" + components.get(0).getComponentName() + "\"," + "\n";
        toReturn += "TARGET: \"" + components.get(1).getComponentName() + "\"," + "\n";
        toReturn += "OFFSETLABEL: 0, 0," + "\n";
        toReturn += "UNIQUE_NAME: \"row1\"," + "\n";
        toReturn += "TRACES_CONNECTION_FILTER {" + "\n";
        for (Column column : columns) {
            toReturn += "   TRACE_COLUMN : \"" + column.name + "\"," + "\n";
            toReturn += "   TRACE_COLUMN_CHECKED : \"true\"," + "\n";
            toReturn += "   TRACE_COLUMN_CONDITION : \"\",\n";
        }
        toReturn = StringsUtils.removeLastChars(toReturn, 2);
        toReturn += "\n}" + "\n";
        toReturn += "}" + "\n";
        jobScript += toReturn;
        return this;
    }

    protected JobScriptBuilder addSubJob() {
        String toReturn = "addSubjob {" + "\n";
        toReturn += "NAME: \"" + components.get(0).getComponentName() + "\"" + "\n";
        toReturn += "SUBJOB_TITLE_COLOR: \"160;190;240\"," + "\n";
        toReturn += "SUBJOB_COLOR: \"220;220;250\"" + "\n";
        toReturn += "}";

        jobScript += toReturn;
        return this;
    }

}
