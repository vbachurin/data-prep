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
package org.talend.dataprep.common;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * created by stef on Dec 16, 2014 Detailled comment
 *
 */
public class EasyFilesTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test method for {@link org.talend.dataprep.common.EasyFiles#getFile(java.lang.String)}.
     * 
     * @throws IOException
     */
    @Test
    public void testGetFile() throws IOException {
        File file = EasyFiles.getFile("EasyFilesTestFile.txt");
        Assert.assertEquals("tagada", Files.readFirstLine(file, Charsets.UTF_8));
    }

    @Test
    public void testGetFileException() throws IOException {
        thrown.expect(IOException.class);

        EasyFiles.getFile("EasyFilesTestFile");
    }

}
