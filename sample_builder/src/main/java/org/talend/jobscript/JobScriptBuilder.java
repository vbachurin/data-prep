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
import org.talend.jobscript.components.TFileInputExcel;
import org.talend.jobscript.components.TLogRow;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class JobScriptBuilder {

    private String jobScript = "";

    @Override
    public String toString() {
        return jobScript;
    }

    public JobScriptBuilder addHeader() {
        String header = "SCRIPT_VERSION=4.2,";

        jobScript += header + "\n";
        return this;
    }

    public JobScriptBuilder addContexts() {
        String toReturn = "DEFAULT_CONTEXT: Default," + "\n";
        toReturn += "ContextType {" + "\n";
        toReturn += "NAME: Default" + "\n";
        toReturn += "}" + "\n";

        jobScript += toReturn;
        return this;
    }

    public JobScriptBuilder addFile(String filename) {
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

    public JobScriptBuilder addParameters() {
        return this.addFile("jobScript_parameters.txt");
    }

    public JobScriptBuilder addInputComponents(List<Column> columns) {
        final TFileInputExcel tFileInputExcel = new TFileInputExcel(
                "/home/stephane/talend/a_trier/test_files/users.xls", "Sheet1");
        this.jobScript += tFileInputExcel.generate(columns);

        final TLogRow tLogRow = new TLogRow();
        this.jobScript += tLogRow.generate(columns);

        return this;
    }

    public JobScriptBuilder addConnection(List<Column> columns) {
        String toReturn = "addConnection {" + "\n";
        toReturn += "TYPE: \"FLOW\"," + "\n";
        toReturn += "NAME: \"row1\"," + "\n";
        toReturn += "METANAME: \"tFileInputExcel_1\"," + "\n";
        toReturn += "SOURCE: \"tFileInputExcel_1\"," + "\n";
        toReturn += "TARGET: \"tLogRow_1\"," + "\n";
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

    public JobScriptBuilder addSubJob() {
        String toReturn = "addSubjob {" + "\n";
        toReturn += "NAME: \"tFileInputExcel_1\"" + "\n";
        toReturn += "SUBJOB_TITLE_COLOR: \"160;190;240\"," + "\n";
        toReturn += "SUBJOB_COLOR: \"220;220;250\"" + "\n";
        toReturn += "}";

        jobScript += toReturn;
        return this;
    }

    public static void main(String[] args) {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("Prenom", "id_String"));
        columns.add(new Column("Nom", "id_String"));
        columns.add(new Column("Arrivee", "id_Date"));
        JobScriptBuilder jobScriptBuilder = new JobScriptBuilder().addHeader().addContexts().addParameters()
                .addInputComponents(columns).addConnection(columns).addSubJob();

        System.out.println(jobScriptBuilder);
    }
}
