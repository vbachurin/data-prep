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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
            String output = Files.readLines(EasyFiles.getFile("customers_1k.json"), Charsets.UTF_8, new LineProcessor<String>() {

                StringBuilder sb = new StringBuilder("[");

                @Override
                public boolean processLine(String line) throws IOException {
                    sb.append(line + ",");
                    return true;
                }

                @Override
                public String getResult() {
                    StringsUtils.removeLastChars(sb, 1);
                    sb.append("]");
                    return sb.toString();
                }
            });

            out.print(output);
        } catch (Exception e) {
            e.printStackTrace(resp.getWriter());
        }
        out.close();
    }
}
