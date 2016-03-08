package org.talend.dataprep.schema.xls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.schema.FormatGuesser;
import org.talend.dataprep.schema.SchemaParser;
import org.talend.dataprep.schema.unsupported.UnsupportedFormatGuess;

@Component("formatGuesser#xls")
public class XlsFormatGuesser implements FormatGuesser {

    private static final Logger LOGGER = LoggerFactory.getLogger(XlsFormatGuesser.class);

    @Autowired
    private XlsFormatGuess xlsFormatGuess;

    /** The fallback guess if the input is not Excel compliant. */
    @Autowired
    private UnsupportedFormatGuess fallbackGuess;

    @Override
    public boolean accept(String encoding) {
        return ObjectUtils.equals(encoding, Charset.forName("UTF-8").name());
    }

    @Override
    public FormatGuesser.Result guess(SchemaParser.Request request, String encoding) {
        if (request == null || request.getContent() == null) {
            throw new IllegalArgumentException("Content cannot be null.");
        }
        boolean xlsFormat = false;
        InputStream inputStream = request.getContent();
        try {

            // wraps the input stream to support mark/reset if needed
            if (!inputStream.markSupported()) {
                inputStream = new PushbackInputStream(inputStream, 8);
            }

            // peek the first 8 bytes (leave the input stream untouched)
            byte[] header8 = IOUtils.peekFirst8Bytes(inputStream);

            if (NPOIFSFileSystem.hasPOIFSHeader(header8)) {
                xlsFormat = true;
            }
            if (!xlsFormat && POIXMLDocument.hasOOXMLHeader(new ByteArrayInputStream(header8))) {
                xlsFormat = true;
            }

        } catch (Exception e) {
            LOGGER.debug("fail to read content, {} does not seem to be an xls file", request.getMetadata().getId(), e);
        }

        return xlsFormat ? new FormatGuesser.Result(xlsFormatGuess, encoding, Collections.emptyMap()) //
                : new FormatGuesser.Result(fallbackGuess, "UTF-8", Collections.emptyMap());
    }
}
