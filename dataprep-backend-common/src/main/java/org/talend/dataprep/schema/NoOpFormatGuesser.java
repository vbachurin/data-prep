package org.talend.dataprep.schema;

import java.io.InputStream;

import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

@Component
public class NoOpFormatGuesser implements FormatGuesser {

    @Transient
    @Override
    public FormatGuess guess(InputStream stream) {
        return new NoOpFormatGuess();
    }

}
