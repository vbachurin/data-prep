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

    private StringBuilder        jobScript  = new StringBuilder();

    public boolean addComponent(InputComponent component) {
        return components.add(component);
    }

    public boolean addColumn(Column column) {
        return columns.add(column);
    }

    public void generateJobScript(File output) throws IOException {
        addHeader();
        addContexts();
        addParameters();
        addInputComponents();
        addConnection();
        addSubJob();
        Files.write(jobScript, output, Charsets.UTF_8);
    }

    @Override
    public String toString() {
        return jobScript.toString();
    }

    protected void addHeader() {
        jobScript.append("SCRIPT_VERSION=4.2,\n");
    }

    protected void addContexts() {
        jobScript.append("DEFAULT_CONTEXT: Default," + "\n");
        jobScript.append("ContextType {" + "\n");
        jobScript.append("NAME: Default" + "\n");
        jobScript.append("}" + "\n");
    }

    protected void addFile(String filename) {
        URL url = this.getClass().getResource(filename);
        try {
            File file = new File(url.toURI());
            for (String currentLine : Files.readLines(file, Charsets.UTF_8)) {
                jobScript.append(currentLine + "\n");
            }
        } catch (IOException | URISyntaxException e) {
            // TODO manage this better
            e.printStackTrace();
        }
    }

    protected void addParameters() {
        addFile("jobScript_parameters.txt");
    }

    protected void addInputComponents() {
        for (InputComponent component : components) {
            jobScript.append(component.generate(columns));
        }
    }

    protected void addConnection() {
        for (int i = 1; i < components.size(); i++) {
            jobScript.append("addConnection {" + "\n");
            jobScript.append("TYPE: \"FLOW\"," + "\n");
            jobScript.append("NAME: \"row" + i + "\"," + "\n");

            jobScript.append("METANAME: \"" + components.get(i - 1).getComponentName() + "\"," + "\n");
            jobScript.append("SOURCE: \"" + components.get(i - 1).getComponentName() + "\"," + "\n");
            jobScript.append("TARGET: \"" + components.get(i).getComponentName() + "\"," + "\n");

            jobScript.append("OFFSETLABEL: 0, 0," + "\n");
            jobScript.append("UNIQUE_NAME: \"row" + i + "\"," + "\n");
            jobScript.append("TRACES_CONNECTION_FILTER {" + "\n");
            for (Column column : columns) {
                jobScript.append("   TRACE_COLUMN : \"" + column.name + "\"," + "\n");
                jobScript.append("   TRACE_COLUMN_CHECKED : \"true\"," + "\n");
                jobScript.append("   TRACE_COLUMN_CONDITION : \"\",\n");
            }
            StringsUtils.removeLastChars(jobScript, 2);
            jobScript.append("\n}" + "\n");
            jobScript.append("}" + "\n");
        }

    }

    protected void addSubJob() {
        jobScript.append("addSubjob {" + "\n");
        jobScript.append("NAME: \"" + components.get(0).getComponentName() + "\"" + "\n");
        jobScript.append("SUBJOB_TITLE_COLOR: \"160;190;240\"," + "\n");
        jobScript.append("SUBJOB_COLOR: \"220;220;250\"" + "\n");
        jobScript.append("}");
    }

}
