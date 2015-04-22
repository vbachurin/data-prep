package org.talend.dataprep.schema;

import java.io.*;
import java.util.*;

import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.CommonMessages;
import org.talend.dataprep.exception.Exceptions;

@Component
public class LineBasedFormatGuesser implements FormatGuesser {

    private static Separator guessSeparator(InputStream is, String encoding) {
        try {
            Reader reader = encoding != null ? new InputStreamReader(is, encoding) : new InputStreamReader(is);
            try (LineNumberReader lineNumberReader = new LineNumberReader(reader)) {
                List<Separator> separators = new ArrayList<>();
                Map<Character, Separator> separatorMap = new HashMap<>();
                int totalChars = 0;
                int lineCount = 0;
                boolean inQuote = false;
                String s;
                while (totalChars < 64 * 1024 && lineCount < 100 && (s = lineNumberReader.readLine()) != null) {
                    totalChars += s.length() + 1; // count the new line character
                    if (s.length() == 0) {
                        continue;
                    }
                    if (!inQuote) {
                        lineCount++;
                    }
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if ('"' == c) {
                            inQuote = !inQuote;
                        }
                        if (!Character.isLetterOrDigit(c) && !"\"' .-".contains(s.subSequence(i, i + 1)) && (!inQuote)) {
                            Separator separator = separatorMap.get(c);
                            if (separator == null) {
                                separator = new Separator();
                                separator.separator = c;
                                separatorMap.put(c, separator);
                                separators.add(separator);
                            }
                            separator.currentLineCount++;
                        }
                    }
                    if (!inQuote) {
                        for (Separator separator : separators) {
                            separator.totalCount += separator.currentLineCount;
                            separator.totalOfSquaredCount += separator.currentLineCount * separator.currentLineCount;
                            separator.currentLineCount = 0;
                        }
                    }
                }
                if (separators.size() > 0) {
                    for (Separator separator : separators) {
                        separator.averagePerLine = separator.totalCount / (double) lineCount;
                        separator.stddev = Math
                                .sqrt((((double) lineCount * separator.totalOfSquaredCount) - (separator.totalCount * separator.totalCount))
                                        / ((double) lineCount * (lineCount - 1)));
                    }
                    Collections.sort(separators,
                            (sep0, sep1) -> Double.compare(sep0.stddev / sep0.averagePerLine, sep1.stddev / sep1.averagePerLine));
                    Separator separator = separators.get(0);
                    if (separator.stddev / separator.averagePerLine < 0.1) {
                        return separator;
                    }
                }
            }
        } catch (IOException e) {
            throw Exceptions.User(CommonMessages.UNABLE_TO_READ_CONTENT, e);
        }
        return null;
    }

    @Override
    public FormatGuess guess(InputStream stream) {
        Separator sep = guessSeparator(stream, "UTF-8");
        if (sep != null) {
            return new CSVFormatGuess(sep);
        }
        return new NoOpFormatGuess(); // Fallback
    }

}