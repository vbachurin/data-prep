// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.encrypt;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class PropertiesEncryptionTest {

    @Test
    public void encryptAndSave() throws Exception {
        String propertyKey = "admin.password";
        String propertyValue = "5ecr3t";
        String propertyEncodedValue = "JP6lC6hVeu3wRZA1Tzigyg==";
        Path tempFile = Files.createTempFile("dataprep-PropertiesEncryptionTest.", ".properties");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(tempFile, Charsets.UTF_8)) {
            bufferedWriter.write(propertyKey + "=" + propertyValue);
        }

        new PropertiesEncryption().encryptAndSave(tempFile.toString(), Sets.newHashSet(propertyKey));

        try (BufferedReader reader = Files.newBufferedReader(tempFile)) {
            assertEquals(propertyKey + "=" + propertyEncodedValue, reader.readLine());
        }
    }

    @Test
    public void decryptAndSave() throws Exception {
        String propertyKey = "admin.password";
        String propertyEncodedValue = "JP6lC6hVeu3wRZA1Tzigyg==";
        String propertyValue = "5ecr3t";
        Path tempFile = Files.createTempFile("dataprep-PropertiesEncryptionTest.", ".properties");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(tempFile, Charsets.UTF_8)) {
            bufferedWriter.write(propertyKey + "=" + propertyEncodedValue);
        }

        new PropertiesEncryption().decryptAndSave(tempFile.toString(), Sets.newHashSet(propertyKey));

        try (BufferedReader reader = Files.newBufferedReader(tempFile)) {
            assertEquals(propertyKey + "=" + propertyValue, reader.readLine());
        }
    }

    @Test
    public void encryptAndSave_doesNotBreakLayout() throws Exception {
        Path tempFile = Files.createTempFile("dataprep-PropertiesEncryptionTest.", ".properties");
        try (InputStream refInStream = getClass().getResourceAsStream("keep-layout-test-in.properties")) {
            Files.copy(refInStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        new PropertiesEncryption().encryptAndSave(tempFile.toString(), Sets.newHashSet("admin.password"));

        assertEquals(Resources.toString(getClass().getResource("keep-layout-test-expected.properties"), Charsets.UTF_8),
                com.google.common.io.Files.toString(tempFile.toFile(), Charsets.UTF_8));

    }

}