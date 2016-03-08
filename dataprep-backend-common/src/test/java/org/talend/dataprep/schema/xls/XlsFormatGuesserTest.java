//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.schema.xls;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.schema.AbstractSchemaTestUtils;
import org.talend.dataprep.schema.FormatGuesser;
import org.talend.dataprep.schema.unsupported.UnsupportedFormatGuess;

/**
 * Unit test for the XlsFormatGuesser
 */
public class XlsFormatGuesserTest extends AbstractSchemaTestUtils {

    /** The format guesser to test. */
    @Autowired
    XlsFormatGuesser guesser;

    @Test
    public void format_guesser_accept() throws Exception {
        assertTrue(guesser.accept("UTF-8"));
        assertFalse(guesser.accept("UTF-16"));
    }

    @Test
    public void shouldNotGuessBecauseEmptyFile() throws IOException {
        FormatGuesser.Result actual = guesser.guess(getRequest(new ByteArrayInputStream(new byte[0]), "#1"), "UTF-8");
        assertTrue(actual.getFormatGuess() instanceof UnsupportedFormatGuess);
    }

    @Test
    public void shouldGuessXls() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("customersDate.xls")) {
            FormatGuesser.Result actual = guesser.guess(getRequest(inputStream, "#2"), "UTF-8");
            assertTrue(actual.getFormatGuess() instanceof XlsFormatGuess);
        }
    }

    @Test
    public void shouldGuessXlsx() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("file_with_header.xlsx")) {
            FormatGuesser.Result actual = guesser.guess(getRequest(inputStream, "#3"), "UTF-8");
            assertTrue(actual.getFormatGuess() instanceof XlsFormatGuess);
        }
    }

    @Test
    public void shouldNotGuessCsv() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("fake.xls")) {
            FormatGuesser.Result actual = guesser.guess(getRequest(inputStream, "#4"), "UTF-8");
            assertTrue(actual.getFormatGuess() instanceof UnsupportedFormatGuess);
        }
    }

    @Test
    public void shouldNotGuessXml() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("simple_workbook.xml")) {
            FormatGuesser.Result actual = guesser.guess(getRequest(inputStream, "#5"), "UTF-8");
            assertTrue(actual.getFormatGuess() instanceof UnsupportedFormatGuess);
        }
    }

    @Test
    public void shouldNotGuessHtml() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("../html/sales-force.xls")) {
            FormatGuesser.Result actual = guesser.guess(getRequest(inputStream, "#6"), "UTF-8");
            assertTrue(actual.getFormatGuess() instanceof UnsupportedFormatGuess);
        }
    }
}