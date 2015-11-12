package org.talend.dataprep.schema;


import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

/**
 * CSV FormatGuesser.
 *
 * Read the first 100 lines or 64k chars to check if given dataset is a CSV as well as its potential separator.
 *
 * Separator are chosen out of
 *
 * @see FormatGuesser
 */
@Component
public class LineBasedFormatGuesser implements FormatGuesser {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LineBasedFormatGuesser.class);

    /** Detectors used to check the encoding. */
    private List<WrongEncodingDetector> detectors = Arrays.asList( //
            new WrongEncodingDetector(65533), //
            new WrongEncodingDetector(0) //
    );

    /** The csv format guesser. */
    @Autowired
    private CSVFormatGuess csvFormatGuess;

    /** The fallback guess if the input is not CSV compliant. */
    @Autowired
    private UnsupportedFormatGuess fallbackGuess;

    /** A list of supported separators for a CSV content */
    private Set<Character> validSeparators = new HashSet<Character>() {
        {
            add(' ');
            add('\t');
            add(',');
            add(';');
        }
    };

    /**
     * @see FormatGuesser#guess(InputStream, String)
     */
    @Override
    public FormatGuesser.Result guess(InputStream stream, String encoding) {
        if (stream == null) {
            throw new IllegalArgumentException("Content cannot be null.");
        }

        Separator sep = guessSeparator(stream, encoding);

        // Fallback
        if (sep == null) {
            return new FormatGuesser.Result(fallbackGuess, "UTF-8", Collections.emptyMap());
        }

        final char separator = sep.getSeparator();
        Map<String, String> parameters = new HashMap<>();
        parameters.put(CSVFormatGuess.SEPARATOR_PARAMETER, String.valueOf(separator));
        return new FormatGuesser.Result(csvFormatGuess, encoding, parameters);
    }

    /**
     * Try to guess the separator used in the CSV.
     *
     * @param is the inputstream to read the CSV from.
     * @param encoding the encoding to use for the reading.
     * @return the guessed CSV separator or null if none found.
     */
    private Separator guessSeparator(InputStream is, String encoding) {
        try {
            Reader reader = encoding != null ? new InputStreamReader(is, encoding) : new InputStreamReader(is);
            try (LineNumberReader lineNumberReader = new LineNumberReader(reader)) {
                Map<Character, Separator> separatorMap = new HashMap<>();
                long totalChars = 0;
                int lineCount = 0;
                boolean inQuote = false;
                String s;
                while (totalChars < 64 * 1024 && lineCount < 100 && (s = lineNumberReader.readLine()) != null) {
                    totalChars += s.length() + 1; // count the new line character
                    if (s.isEmpty()) {
                        continue;
                    }
                    if (!inQuote) {
                        lineCount++;
                    }
                    for (int i = 0; i < s.length(); i++) {

                        char c = s.charAt(i);

                        // check the encoding
                        try {
                            checkEncoding(c, totalChars);
                        } catch (IOException e) {
                            LOGGER.debug(encoding + " is assumed wrong" + e);
                            return null;
                        }

                        if ('"' == c) {
                            inQuote = !inQuote;
                        }

                        if (!inQuote) {
                            processCharAsSeparatorCandidate(c, separatorMap, lineCount);
                        }

                    }
                }
                return chooseSeparator(new ArrayList<>(separatorMap.values()), lineCount);
            }
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_READ_CONTENT, e);
        } catch (Exception e) {
            LOGGER.debug("Unable to read content from content using encoding '{}'.", encoding, e);
            return null;
        }
    }

    /**
     * Check the encoding with every WrongEncodingDetector.
     *
     * @param c the current char to check.
     * @param totalChars the total number of chars so far.
     * @throws IOException if the encoding is assumed wrong.
     */
    private void checkEncoding(char c, long totalChars) throws IOException {
        for (WrongEncodingDetector detector : detectors) {
            detector.checkChar(c, totalChars);
        }
    }

    /**
     * Detects if the given char is a separator candidate. If true, the separator is added within the separators map.
     *
     * @param candidate the candidate to analyse.
     * @param separatorMap the map of current candidates.
     * @param lineNumber the current line number.
     */
    protected void processCharAsSeparatorCandidate(char candidate, Map<Character, Separator> separatorMap, int lineNumber) {
        if (!Character.isLetterOrDigit(candidate)) {
            Separator separator = separatorMap.get(candidate);
            if (separator == null) {
                separator = new Separator(candidate);
                separatorMap.put(candidate, separator);
            }
            separator.incrementCount(lineNumber);
        }
    }

    /**
     * Choose the best separator out of the given ones.
     *
     * @param separators the list of separators found in the CSV (may be empty but not null.
     * @param lineCount number of lines in the CSV.
     * @return the separator to use to read the CSV or null if none found.
     */
    private Separator chooseSeparator(List<Separator> separators, int lineCount) {

        // easy case where there's no choice
        if (separators.isEmpty()) {
            if (lineCount > 0) {
                // There are some lines processed, but no separator (a one-column content?), so pick a default separator.
                return new Separator(',');
            }
            return null;
        }

        // if there's only one separator, let's use it
        if (separators.size() == 1) {
            return separators.get(0);
        }

        // compute the score for each separator
        separators.forEach(s -> {

            // compute average per line
            double averagePerLine = s.getTotalCount() / lineCount;
            s.setAveragePerLine(averagePerLine);

            // compute the standard deviation
            double sum = 0;
            for (int currentLine = 0; currentLine < lineCount; currentLine++) {
                final double currentLineCount = s.getCount(lineCount);
                sum += currentLineCount * Math.pow(currentLine - averagePerLine, 2);
            }
            s.setStandardDeviation(sum / s.getTotalCount());

        });

        // filter and sort separators
        return separators.stream()
.filter(separator -> separator.getAveragePerLine() > 0) // remove irrelevant
                                                                                          // separators
                .sorted((s0, s1) -> Double.compare(s1.getAveragePerLine(), s0.getAveragePerLine())) // sort by average
                .sorted((s0, s1) -> Double.compare(s0.getStandardDeviation(), s1.getStandardDeviation())) // sort by
                                                                                                          // sddev
                .filter(sep -> validSeparators.contains(sep.getSeparator())) // filter out invalid separators
                .findFirst() //
                .get();

    }

    /**
     * Count the number of 'informant char' found in the file. If this number exceeds the threshold (10 %) the encoding
     * is assumed false.
     */
    private class WrongEncodingDetector {

        /** Threshold to detect binary stream in percentage. */
        private static final int WRONG_ENCODING_THRESHOLD = 10;

        /** Char informing that the encoding is supposed to be wrong. */
        private int informantChar;

        /** How many time was the informant char found. */
        private long count = 0;

        /**
         * Default constructor.
         * 
         * @param informantChar the char to use to detect wrong encoding.
         */
        public WrongEncodingDetector(int informantChar) {
            this.informantChar = informantChar;
        }

        /**
         * Check the given char.
         * 
         * @param read the char that was read.
         * @param totalChars the total number of chars.
         * @throws IOException if encoding is assumed false.
         */
        public void checkChar(char read, long totalChars) throws IOException {

            if (informantChar != (int) read) {
                return;
            }

            count++;
            long percentage = count * 100 / totalChars;
            if (percentage > WRONG_ENCODING_THRESHOLD) {
                LOGGER.debug("wrong encoding detected, hence cannot be a CSV");
                throw new IOException(
                        "'" + (char) informantChar + "' is found more than " + WRONG_ENCODING_THRESHOLD + " % in file.");
            }
        }

    }

}