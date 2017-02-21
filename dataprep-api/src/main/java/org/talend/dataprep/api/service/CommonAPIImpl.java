// ============================================================================
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

package org.talend.dataprep.api.service;

import static org.apache.commons.collections.IteratorUtils.chainedIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.talend.daikon.annotation.Client;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.exception.error.ErrorCode;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.services.dataprep.DataSetService;
import org.talend.services.dataprep.PreparationService;
import org.talend.services.dataprep.TransformationService;
import org.talend.services.dataprep.api.CommonAPI;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

@ServiceImplementation
public class CommonAPIImpl extends APIService implements CommonAPI {

    @Client
    TransformationService transformationService;

    @Client
    DataSetService dataSetService;

    @Client
    PreparationService preparationService;

    @Override
    public void listErrors(final OutputStream output) throws IOException {

        LOG.debug("Listing supported error codes");

        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(output);
        generator.setCodec(mapper);

        // start the errors array
        generator.writeStartArray();

        // write the direct known errors
        writeErrorsFromEnum(generator, CommonErrorCodes.values());
        writeErrorsFromEnum(generator, APIErrorCodes.values());

        Iterator errorCodes = chainedIterator(
                chainedIterator(
                        transformationService.listErrors().iterator(),
                        dataSetService.listErrors().iterator()
                ),
                preparationService.listErrors().iterator()
        );
        while (errorCodes.hasNext()) {
            final JsonErrorCodeDescription description = (JsonErrorCodeDescription) errorCodes.next();
            generator.writeObject(description);
        }

        // close the errors array
        generator.writeEndArray();
        generator.flush();
    }

    @Override
    public Type[] listTypes() throws IOException {
        LOG.debug("Listing supported types");
        return Type.values();
    }

    /**
     * Write the given error codes to the generator.
     *
     * @param generator the json generator to use.
     * @param codes the error codes to write.
     * @throws IOException if an error occurs.
     */
    private void writeErrorsFromEnum(JsonGenerator generator, ErrorCode[] codes) throws IOException {
        for (ErrorCode code : codes) {
            // cast to JsonErrorCode needed to ease json handling
            JsonErrorCodeDescription description = new JsonErrorCodeDescription(code);
            generator.writeObject(description);
        }
    }
}
