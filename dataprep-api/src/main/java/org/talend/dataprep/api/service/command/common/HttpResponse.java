package org.talend.dataprep.api.service.command.common;

/**
 * Simple model for an http response to be able to transfer status code and content from the command to the API.
 * <b>Avoid to use it for large String response and prefer pipe stream</b>
 */
public class HttpResponse {

    private final int statusCode;

    private final String httpContent;

    private final String contentType;

    public HttpResponse(int statusCode, String httpContent, String contentType) {
        this.httpContent = httpContent;
        this.statusCode = statusCode;
        this.contentType = contentType;
    }

    public String getContentType()
    {
        return contentType;
    }

    public String getHttpContent() {
        return httpContent;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
