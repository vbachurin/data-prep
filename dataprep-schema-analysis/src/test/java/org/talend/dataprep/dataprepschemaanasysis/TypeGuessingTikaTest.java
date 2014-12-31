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
package org.talend.dataprep.dataprepschemaanasysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.Tika;
import org.junit.Test;
import org.talend.dataprep.common.EasyFiles;

/**
 * created by stef on Dec 22, 2014 Detailled comment
 *
 */
public class TypeGuessingTikaTest {

    private String analyzeFile(File file) throws IOException {
        return " - " + file.getName() + " -> " + new Tika().detect(new FileInputStream(file));
    }

    @Test
    public void testGuessFileTypeCsv() throws IOException {
        System.out.println(analyzeFile(EasyFiles.getFile("tagada.csv")));
        System.out.println(analyzeFile(EasyFiles.getFile("tagada.xls")));
        System.out.println(analyzeFile(EasyFiles.getFile("tagada.json")));
        System.out.println(analyzeFile(EasyFiles.getFile("tagada.csv.named.xls")));
    }
}
