package org.talend.dataprep.schema;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("formatGuesser#xls")
public class XlsFormatGuesser implements FormatGuesser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private XlsFormatGuess xlsFormatGuess;

    @Override
    public FormatGuess guess(InputStream stream) {

        try {

            Workbook workbook = XlsUtils.getWorkbook(stream);

            // if poi can read it we assume it's correct excel file
            // && at least one sheet
            if (workbook.getNumberOfSheets() > 0) {
                return xlsFormatGuess;
            }
        } catch (IOException e) {
            logger.debug("fail to read content: " + e.getMessage(), e);
        }

        return new NoOpFormatGuess();
    }
}
