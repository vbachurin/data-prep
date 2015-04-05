package org.talend.dataprep.schema;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("formatGuesser#xls")
public class XlsFormatGuesser implements FormatGuesser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public FormatGuess guess(InputStream stream) {

        try {

            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(stream);
            // if poi can read we assume it's correct excel file
            // && at least one sheet
            if (hssfWorkbook.getNumberOfSheets() > 0) {
                new XlsFormatGuess();
            }

        } catch (IOException e) {
            logger.debug("fail to read content: " + e.getMessage(), e);
        }

        return new NoOpFormatGuess(); // Fallback
    }
}
