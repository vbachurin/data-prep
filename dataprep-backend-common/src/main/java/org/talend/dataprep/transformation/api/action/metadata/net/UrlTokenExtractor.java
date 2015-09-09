package org.talend.dataprep.transformation.api.action.metadata.net;

import java.net.URI;

import org.talend.dataprep.api.type.Type;

/**
 * Interface for all url token extractor.
 */
public interface UrlTokenExtractor {
    /**
     * @return the token name.
     */
    String getTokenName();

    /**
     * @param url the url to extract the token from.
     * @return the extracted token.
     */
    String extractToken(URI url);

    /**
     * @return token type (default is String).
     */
    default Type getType() {
        return Type.STRING;
    }
}
