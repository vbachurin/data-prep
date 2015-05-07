package org.talend.dataprep.schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.schema.io.XlsUtils;

@Component("formatGuesser#xls")
public class XlsFormatGuesser implements FormatGuesser {

    private static final Logger LOGGER = LoggerFactory.getLogger(XlsFormatGuesser.class);

    @Autowired
    private XlsFormatGuess xlsFormatGuess;

    /** The fallback guess if the input is not CSV compliant. */
    @Autowired
    private NoOpFormatGuess fallbackGuess;

    @Override
    public FormatGuesser.Result guess(InputStream stream) {
        try {
            Workbook workbook = XlsUtils.getWorkbook(stream);
            // if poi can read it we assume it's correct excel file
            // && at least one sheet
            if (workbook.getNumberOfSheets() > 0) {
                return new FormatGuesser.Result(xlsFormatGuess, Collections.emptyMap());
            }
        } catch (IOException e) {
            LOGGER.debug("fail to read content: " + e.getMessage(), e);
        }
        return new FormatGuesser.Result(fallbackGuess, Collections.emptyMap());
    }
}
