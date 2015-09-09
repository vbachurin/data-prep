package org.talend.dataprep.schema;

import java.io.InputStream;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnsupportedFormatGuesser implements FormatGuesser {

    @Autowired
    private UnsupportedFormatGuess noOpFormatGuess;

    @Override
    public FormatGuesser.Result guess(InputStream stream) {
        return new FormatGuesser.Result(noOpFormatGuess, Collections.emptyMap());
    }

}
