package org.talend.dataprep.metrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.http.Part;

class PartWrapper implements Part, Metered {

    private final Part delegate;

    private MeteredInputStream meteredInputStream;

    PartWrapper(Part delegate) {
        this.delegate = delegate;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (meteredInputStream == null) {
            meteredInputStream = new MeteredInputStream(delegate.getInputStream());
        }
        return meteredInputStream;
    }

    public String getContentType() {
        return delegate.getContentType();
    }

    public String getName() {
        return delegate.getName();
    }

    public String getSubmittedFileName() {
        return delegate.getSubmittedFileName();
    }

    public long getSize() {
        return delegate.getSize();
    }

    public void write(String fileName) throws IOException {
        delegate.write(fileName);
    }

    public void delete() throws IOException {
        delegate.delete();
    }

    public String getHeader(String name) {
        return delegate.getHeader(name);
    }

    public Collection<String> getHeaders(String name) {
        return delegate.getHeaders(name);
    }

    public Collection<String> getHeaderNames() {
        return delegate.getHeaderNames();
    }

    @Override
    public long getVolume() {
        if (meteredInputStream == null) {
            return 0;
        }
        return meteredInputStream.getVolume();
    }
}
