package org.talend.dataprep.number;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import static org.junit.Assert.*;

/**
 * Created by stef on 19/01/16.
 */
public class BigDecimalParserTest {

    @Test
    public void testToBigDecimal_US() throws Exception {
        assertEquals(new BigDecimal("12.5"), BigDecimalParser.toBigDecimal("0012.5"));
    }

    @Test(expected = ParseException.class)
    public void testToBigDecimal_not_a_number() throws Exception {
        BigDecimalParser.toBigDecimal("ouf");
    }


    @Test
    public void testToBigDecimal_US_precision() throws Exception {
        assertEquals(0.35, BigDecimalParser.toBigDecimal("0.35").doubleValue(), 0);
    }

    @Test
    public void testToBigDecimal_US_thousand_group() throws Exception {
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10,012.5"));
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10,012.5"));
        assertEquals(new BigDecimal("10012"), BigDecimalParser.toBigDecimal("10,012"));
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10012.5"));
    }

    @Test
    public void testToBigDecimal_US_negative_1() throws Exception {
        assertEquals(new BigDecimal("-12.5"), BigDecimalParser.toBigDecimal("-12.5"));
    }

    @Test
    public void testToBigDecimal_US_negative_2() throws Exception {
        assertEquals(new BigDecimal("-12.5"), BigDecimalParser.toBigDecimal("(12.5)"));
    }

    @Test
    public void testToBigDecimal_EU() throws Exception {
        assertEquals(new BigDecimal("12.5"), BigDecimalParser.toBigDecimal("0012,5", ',', ' '));
    }

    @Test
    public void testToBigDecimal_EU_thousand_group() throws Exception {
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10 012,5", ',', ' '));
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10012,5", ',', ' '));
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10.012,5", ',', '.'));
        assertEquals(new BigDecimal("10012"), BigDecimalParser.toBigDecimal("10 012", ',', ' '));
    }

    @Test
    public void testToBigDecimal_EU_thousand_group_better_guess() throws Exception {
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10 012,5"));
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10012,5"));
        assertEquals(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10.012,5"));
        assertEquals(new BigDecimal("12.5678"), BigDecimalParser.toBigDecimal("12,5678"));
    }

    @Test
    public void testToBigDecimal_EU_negative() throws Exception {
        assertEquals(new BigDecimal("-12.5"), BigDecimalParser.toBigDecimal("-12,5", ',', ' '));
    }

    @Test
    public void testToBigDecimal_scientific() throws Exception {
        assertEquals(new BigDecimal("1230").toString(), BigDecimalParser.toBigDecimal("1.23E+3").toPlainString());
        assertEquals(new BigDecimal("1235.2").toString(), BigDecimalParser.toBigDecimal("1.2352E+3").toPlainString());
    }

    @Test
    public void testGuessSeparators_two_different_separators_present() {
        testGuessSeparators("1,045.5", '.', ',');
        testGuessSeparators("1 045,5", ',', ' ');
        testGuessSeparators("1.045,5", ',', '.');

        testGuessSeparators("2.051.045,5", ',', '.');
        testGuessSeparators("2 051 045,5", ',', ' ');
        testGuessSeparators("2,051,045.5", '.', ',');
    }

    @Test
    public void testGuessSeparators_many_group_sep() {
        testGuessSeparators("2.051.045", ',', '.');
        testGuessSeparators("2 051 045", '.', ' ');
        testGuessSeparators("2,051,045", '.', ',');
    }

    @Test
    public void testGuessSeparators_starts_with_decimal_sep() {
        testGuessSeparators(".045", '.', ',');
        testGuessSeparators(",045", ',', '.');
    }

    @Test
    public void testGuessSeparators_no_group() {
        testGuessSeparators("1045,5", ',', '.');
        testGuessSeparators("2051045,5", ',', '.');
        testGuessSeparators("1234,888", ',', '.');
    }

    @Test
    public void testGuessSeparators_not_end_by_3_digits() {
        testGuessSeparators("45,5", ',', '.');
        testGuessSeparators("45,55", ',', '.');
        testGuessSeparators("45,5555", ',', '.');
    }

    private void testGuessSeparators(String value, char expectedDecimalSeparator, char expectedGroupingSeparator) {
        DecimalFormatSymbols decimalFormatSymbols = BigDecimalParser.guessSeparators(value);
        assertEquals(expectedGroupingSeparator, decimalFormatSymbols.getGroupingSeparator());
        assertEquals(expectedDecimalSeparator, decimalFormatSymbols.getDecimalSeparator());

        // Assert that negative symbol doesn't affect guess:
        decimalFormatSymbols = BigDecimalParser.guessSeparators("-" + value);
        assertEquals(expectedGroupingSeparator, decimalFormatSymbols.getGroupingSeparator());
        assertEquals(expectedDecimalSeparator, decimalFormatSymbols.getDecimalSeparator());
    }

}
