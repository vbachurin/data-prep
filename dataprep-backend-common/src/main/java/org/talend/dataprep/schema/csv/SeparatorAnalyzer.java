package org.talend.dataprep.schema.csv;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.util.ShannonEntropy;

/**
 * This class performs header and scoring analysis on a separator. It uses the <tt>CSVFastHeaderAndTypeAnalyzer</tt> to
 * determine the header. It also associates a score to a separator using a heuristic based upon the
 * <a href="https://en.wikipedia.org/wiki/Entropy_(information_theory)">Shanon entropy</a>. The entropy of the separator
 * is computed according to the specified number of lines used as the sample size.
 */
public class SeparatorAnalyzer implements Consumer<Separator> {

    /** Number of lines read in the dataset. */
    private int numberOfLines;

    /**
     * Some sample lines used to make the score more accurate.
     */
    private List<String> sampleLines;

    /**
     * Constructor.
     *
     * @param numberOfLines the number of lines.
     */
    public SeparatorAnalyzer(int numberOfLines, List<String> sampleLines) {
        if (numberOfLines <= 0) {
            throw new IllegalArgumentException("The number of lines must be strictly positive");
        }
        this.numberOfLines = numberOfLines;
        this.sampleLines = sampleLines;
    }

    /**
     * Returns the frequency of each count (per line) of the separator.
     * 
     * @param separator
     * @return
     */
    private Collection<Double> countFrequency(Separator separator) {
        HashMap<Long, Long> countOccurrences = new HashMap<>();

        if (separator.getCountPerLine().isEmpty()) {
            separator.setScore(Double.MAX_VALUE);
            return Collections.emptyList();
        }
        // count the number of occurrence of each count
        for (Long separatorCount : separator.getCountPerLine().values()) {
            // increment the number of occurrence of the separatorCount
            Long separatorCountOccurrence = countOccurrences.get(separatorCount);
            separatorCountOccurrence = separatorCountOccurrence == null ? 1L : separatorCountOccurrence + 1;
            countOccurrences.put(separatorCount, separatorCountOccurrence);
        }
        int zeroCount = numberOfLines - separator.getCountPerLine().size();
        if (zeroCount > 0) {
            countOccurrences.put(0L, (long) zeroCount);
        }
        return countOccurrences.values().stream().mapToDouble(s -> (double) s / numberOfLines).boxed()
                .collect(Collectors.toList());
    }

    /**
     * Computes the entropy of a separator.
     * 
     * The lower the entropy, the better, i.e, the number of occurrence of the separator remains stable while processing
     * the lines, which means that the separator has a huge probability of being the wanted one.
     */
    private double computeEntropy(Separator separator) {
        final Collection<Double> countFrequencies = countFrequency(separator);
        return ShannonEntropy.computeWithFrequencies(countFrequencies);
    }

    /**
     * @see Consumer#accept(Object)
     *
     *
     */
    @Override
    public void accept(Separator separator) {
        separator.setScore(computeEntropy(separator));
        CSVFastHeaderAndTypeAnalyzer csvFastHeaderAndTypeAnalyzer = new CSVFastHeaderAndTypeAnalyzer(sampleLines, separator);
        csvFastHeaderAndTypeAnalyzer.analyze();
        separator.setFirstLineAHeader(true);
        separator.setConsistent(true);
        LinkedHashMap<String, Type> header = new LinkedHashMap<>();
        for (String s : csvFastHeaderAndTypeAnalyzer.getHeaders().keySet()) {
            header.put(s, Type.STRING);
        }
        separator.setHeaders(header);
    }
}