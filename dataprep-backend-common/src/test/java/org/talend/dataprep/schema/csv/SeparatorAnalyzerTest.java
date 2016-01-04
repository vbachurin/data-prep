package org.talend.dataprep.schema.csv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for Separator analyzer.
 */
public class SeparatorAnalyzerTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckLineNumber() throws Exception {
        new SeparatorAnalyzer(-1, null);
    }

    @Test
    public void shouldSetDefaultScore() {
        SeparatorAnalyzer analyzer = new SeparatorAnalyzer(100, null);
        Separator sep = new Separator('|');
        analyzer.accept(sep);
        assertEquals(Double.MAX_VALUE, sep.score, 0.000000001);
    }

    @Test
    public void shouldComputeEntropy() {

        // given
        Separator sep = new Separator('s');
        incrementCount(12, 1, sep);
        incrementCount(10, 2, sep);
        // nothing on the third line
        incrementCount(11, 4, sep);
        incrementCount(13, 5, sep);
        incrementCount(12, 6, sep);
        Separator sep2 = new Separator(' ');
        incrementCount(1, 1, sep2);
        incrementCount(1, 2, sep2);
        incrementCount(1, 3, sep2);
        incrementCount(1, 4, sep2);
        incrementCount(1, 5, sep2);
        incrementCount(1, 6, sep2);

        // when
        SeparatorAnalyzer entropy = new SeparatorAnalyzer(6, null);
        entropy.accept(sep);
        entropy.accept(sep2);

        // then
        assertEquals(1.0818020196976097, sep.score, 0.000000001);
        assertEquals(0, sep2.score, 0.0);
    }

    private void incrementCount(int count, int lineNumber, Separator separator) {
        for (int i = 0; i < count; i++) {
            separator.incrementCount(lineNumber);
        }
    }

}