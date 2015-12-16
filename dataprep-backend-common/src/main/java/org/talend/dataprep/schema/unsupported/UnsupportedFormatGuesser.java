package org.talend.dataprep.schema.unsupported;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.schema.FormatGuesser;
import org.talend.dataprep.schema.SchemaParser;

@Component
public class UnsupportedFormatGuesser implements FormatGuesser {

    @Autowired
    private UnsupportedFormatGuess noOpFormatGuess;

    @Override
    public FormatGuesser.Result guess(SchemaParser.Request request, String encoding) {
        return new FormatGuesser.Result(noOpFormatGuess, "UTF-8", Collections.emptyMap());
    }

}
