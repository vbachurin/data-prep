// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.schema.html;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.schema.FormatGuesser;
import org.talend.dataprep.schema.SchemaParser;
import org.talend.dataprep.schema.unsupported.UnsupportedFormatGuess;

@Component("formatGuesser#html")
public class HtmlFormatGuesser implements FormatGuesser {

    /** HTML header selector parameter key. */
    public static final String HEADER_SELECTOR_KEY = "html.HEADER_SELECTOR_KEY";

    /** HTML value selector parameter key. */
    public static final String VALUES_SELECTOR_KEY = "html.VALUES_SELECTOR_KEY";

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlFormatGuesser.class);

    /** List of patterns to use to parse datasets out of html. */
    private List<Pattern> patterns;

    @Autowired
    private HtmlFormatGuess htmlFormatGuess;

    /** The fallback guess if the input is not Excel compliant. */
    @Autowired
    private UnsupportedFormatGuess fallbackGuess;

    /**
     * Default empty constructor.
     */
    public HtmlFormatGuesser() {
        patterns = new ArrayList<>(1);
        patterns.add(new Pattern("html body table tr th", "html body table tr td"));
    }

    @Override
    public boolean accept(String encoding) {
        return ObjectUtils.equals(encoding, Charset.forName("UTF-8").name());
    }

    /**
     * @see FormatGuesser#guess(SchemaParser.Request, String)
     */
    @Override
    public Result guess(SchemaParser.Request request, String encoding) {

        if (request == null || request.getContent() == null) {
            throw new IllegalArgumentException("Content cannot be null.");
        }

        try {
            for (Pattern pattern : patterns) {

                HeadersContentHandler headersContentHandler = new HeadersContentHandler(pattern.getHeaderSelector(), true);

                HtmlParser htmlParser = new HtmlParser();

                Metadata metadata = new Metadata();

                InputStream inputStream = request.getContent();

                try {
                    // with Tika no need to force the encoding the parser will discover it
                    htmlParser.parse(inputStream, headersContentHandler, metadata, new ParseContext());
                } catch (HeadersContentHandler.HeadersContentFoundException e) { // NOSONAR
                    // exception is here to stop the SAX processing
                    LOGGER.debug("headers for {} found -> {}", request.getMetadata().getId(), headersContentHandler);
                    // save patterns found for the schema parser
                    Map<String, String> parameters = new HashMap<>(2);
                    parameters.put(HEADER_SELECTOR_KEY, pattern.getHeaderSelector());
                    parameters.put(VALUES_SELECTOR_KEY, pattern.getValuesSelector());
                    return new FormatGuesser.Result(htmlFormatGuess, encoding, parameters);
                }
            }

        } catch (Exception e) {
            LOGGER.debug("fail to read content: " + e.getMessage(), e);
        }

        return new Result(fallbackGuess, "UTF-8", Collections.emptyMap());
    }

    /**
     * @return the patterns.
     */
    protected List<Pattern> getPatterns() {
        return patterns;
    }

    /**
     * Class used to setup a HeadersContentHandler.
     */
    static class Pattern {

        /** CSS like selector to get the header out of the html. */
        private String headerSelector;

        /** CSS like selector to get the values out of the html. */
        private String valuesSelector;

        /**
         * Constructor.
         *
         * @param headerSelector selector to get the header out of the html.
         * @param valuesSelector selector to get the values out of the html.
         */
        public Pattern(String headerSelector, String valuesSelector) {
            this.headerSelector = headerSelector;
            this.valuesSelector = valuesSelector;
        }

        /**
         * @return the css like selector used to get the headers.
         */
        public String getHeaderSelector() {
            return headerSelector;
        }

        /**
         * @return the css like selector used to get the values.
         */
        public String getValuesSelector() {
            return valuesSelector;
        }

        /**
         * @see Object#toString()
         */
        @Override
        public String toString() {
            return "Pattern{" + "headerSelector='" + headerSelector + '\'' + ", valuesSelector='" + valuesSelector + '\'' + '}';
        }
    }
}
