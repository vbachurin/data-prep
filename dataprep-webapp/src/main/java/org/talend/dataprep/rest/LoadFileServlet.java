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
package org.talend.dataprep.rest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.talend.dataprep.common.EasyFiles;
import org.talend.dataprep.common.StringsUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/**
 * created by stef on Dec 15, 2014 Detailled comment
 *
 */
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        try {
            File records = EasyFiles.getFile("customers_1k.json");

            out.print(buildFullJson(records));
        } catch (Exception e) {
            e.printStackTrace(resp.getWriter());
        }
        out.close();
    }

    protected static String buildFullJson(File records) throws IOException {
        String output = Files.readLines(records, Charsets.UTF_8, new LineProcessor<String>() {

            int n = 0;

            StringBuilder sb = new StringBuilder("[");

            @Override
            public boolean processLine(String line) throws IOException {
                sb.append(line + ",");
                n++;
                return (n < 50);
            }

            @Override
            public String getResult() {
                StringsUtils.removeLastChars(sb, 1);
                sb.append("]");
                return sb.toString();
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode oNode = JsonNodeFactory.instance.objectNode();
        oNode.put("records", mapper.readTree(output));
        ArrayNode columns = oNode.putArray("columns");

        JsonNode firstRecord = oNode.get("records").iterator().next();

        Iterator<String> fields = firstRecord.getFieldNames();
        while (fields.hasNext()) {
            String column = fields.next();
            ObjectNode cNode = JsonNodeFactory.instance.objectNode();
            cNode.put("id", column);

            switch (column) {
            case "id":
            case "nbCommands":
                cNode.put("type", "integer");
                break;
            case "firstname":
            case "lastname":
            case "state":
            case "city":
                cNode.put("type", "string");
                break;
            case "registration":
            case "birth":
                cNode.put("type", "date");
                break;
            case "avgAmount":
                cNode.put("type", "float");
                break;
            default:
                break;
            }
            columns.add(cNode);

            ObjectNode quality = JsonNodeFactory.instance.objectNode();
            quality.put("valid", 580);
            quality.put("invalid", 8);
            quality.put("empty", 22);
            cNode.put("quality", quality);
        }

        return oNode.toString();
    }
}
