package org.talend.dataprep.dataset.service.analysis.schema;

import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class NoOpFormatGuesser implements FormatGuesser {

    @Transient
    @Override
    public FormatGuess guess(InputStream stream) {
        return new NoOpFormatGuess();
    }

}
