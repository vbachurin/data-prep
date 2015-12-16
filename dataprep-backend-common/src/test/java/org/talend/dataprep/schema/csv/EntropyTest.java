package org.talend.dataprep.schema.csv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for entropy.
 */
public class EntropyTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckLineNumber() throws Exception {
        new Entropy(-1);
    }

    @Test
    public void shouldSetDefaultScore() {
        Entropy entropy = new Entropy(100);
        Separator sep = new Separator('|');
        entropy.accept(sep);
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
        Entropy entropy = new Entropy(6);
        entropy.accept(sep);
        entropy.accept(sep2);

        // then
        assertEquals(0.1458016414, sep.score, 0.000000001);
        assertEquals(0, sep2.score, 0.0);
    }

    private void incrementCount(int count, int lineNumber, Separator separator) {
        for (int i = 0; i < count; i++) {
            separator.incrementCount(lineNumber);
        }
    }

}