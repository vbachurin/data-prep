package org.talend.dataprep.metrics;

import java.io.IOException;
import java.io.InputStream;

class MeteredInputStream extends InputStream {

    private final InputStream delegate;

    private long volume;

    MeteredInputStream(InputStream delegate) {
        this.delegate = delegate;
    }

    private void updateVolume(int read) {
        if (read > 0) {
            volume += read;
        }
    }

    @Override
    public int read() throws IOException {
        try {
            return delegate.read();
        } finally {
            volume++;
        }
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        int read = delegate.read(bytes);
        updateVolume(read);
        return read;
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        int read = delegate.read(bytes, i, i1);
        updateVolume(read);
        return read;
    }

    @Override
    public long skip(long l) throws IOException {
        return delegate.skip(l);
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void mark(int i) {
        delegate.mark(i);
    }

    @Override
    public void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }

    public long getVolume() {
        return volume;
    }
}
