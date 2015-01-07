package org.talend.dataprep.dataset.service.analysis.schema;

import java.io.InputStream;

public interface FormatGuesser {

    public FormatGuess guess(InputStream stream);
}
