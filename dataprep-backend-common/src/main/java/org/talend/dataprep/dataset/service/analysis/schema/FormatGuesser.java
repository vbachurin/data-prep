package org.talend.dataprep.dataset.service.analysis.schema;

import java.io.InputStream;

/**
 * Represents a class able to create {@link org.talend.dataprep.dataset.service.analysis.schema.FormatGuess} from a data
 * set content.
 */
public interface FormatGuesser {

    /**
     * Guess the content type of the provided stream.
     *
     * @param stream The raw data set content.
     * @return A {@link org.talend.dataprep.dataset.service.analysis.schema.FormatGuess guess} that can never be null
     * (see {@link FormatGuess#getConfidence()}.
     */
    default FormatGuess guess(InputStream stream) {
        return null;
    }
}
