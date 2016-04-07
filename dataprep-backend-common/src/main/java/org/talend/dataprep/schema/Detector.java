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

package org.talend.dataprep.schema;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

public interface Detector {

    /**
     * Reads an input stream and detects its format.
     *
     * Note that the stream will not close the specified stream before returning. It is to the responsibility of the
     * caller to close it.
     * 
     * @param metadata the specified TIKA {@link Metadata}
     * @param inputStream the specified input stream
     * @return either null or the detected format
     * @throws IOException
     */
    Format detect(Metadata metadata, TikaInputStream inputStream) throws IOException;

    /**
     * Reads an input stream and detects its format. It creates a new metadata TIKA {@link Metadata} object.
     *
     * Note that the the specified input stream will be closed before returning.
     * 
     * @param inputStream the specified input stream
     * @return either null or the detected format
     * @throws IOException
     */
    default Format detect(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("The specified input stream for a format detection must not be null!");
        }

        TemporaryResources tmp = new TemporaryResources(); // NOSONAR
        // all the depending resources will be closed
        try (TikaInputStream tis = TikaInputStream.get(inputStream, tmp)) {
            return detect(new Metadata(), tis);
        }
    }

}
