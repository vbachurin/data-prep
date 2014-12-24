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

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.talend.dataprep.common.EasyFiles;

/**
 * created by stef on Dec 22, 2014 Detailled comment
 *
 */
public class TypeGuessingTest {

    @Test
    public void testGuessFileTypeCsv() throws FileNotFoundException {
        assertEquals(KnownTypes.CSV, TypeGuessing.guessFileType(EasyFiles.getFile("tagada.csv")));
    }

    @Test
    public void testGuessFileTypeXls() throws FileNotFoundException {
        assertEquals(KnownTypes.XLS, TypeGuessing.guessFileType(EasyFiles.getFile("tagada.xls")));
    }
}
