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

    @Override
    public String getContentType() {
        return delegate.getContentType();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getSubmittedFileName() {
        return delegate.getSubmittedFileName();
    }

    @Override
    public long getSize() {
        return delegate.getSize();
    }

    @Override
    public void write(String fileName) throws IOException {
        delegate.write(fileName);
    }

    @Override
    public void delete() throws IOException {
        delegate.delete();
    }

    @Override
    public String getHeader(String name) {
        return delegate.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return delegate.getHeaders(name);
    }

    @Override
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
