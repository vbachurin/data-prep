//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.api.service.command.preparation;

import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.command.Defaults.pipeStream;
import static org.talend.dataprep.exception.error.PreparationErrorCodes.UNABLE_TO_READ_PREPARATION;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.command.common.ChainedCommand;
import org.talend.dataprep.command.preparation.PreparationDetailsGet;
import org.talend.dataprep.exception.TDPException;

/**
 * Command used to retrieve the preparation content.
 */
@Component
@Scope("request")
public class PreparationGetContent extends ChainedCommand<InputStream, InputStream> {

    /** The preparation id. */
    private final String id;

    /** The preparation version. */
    private final String version;

    /** Optional sample size (if null or <=0, the full preparation content is returned). */
    private Long sample;

    /**
     * Private constructor to ensure the IoC.
     *
     * @param id the preparation id.
     * @param version the preparation version.
     * @param input source command to get the preparation.
     */
    private PreparationGetContent(String id, String version, PreparationDetailsGet input) {
        this(id, version, null, input);
    }

    /**
     * Constructor with sample size specified.
     *
     * @param id the preparation id.
     * @param version the preparation version.
     * @param sample the optional sample value.
     * @param input source command to get the preparation.
     */
    private PreparationGetContent(String id, String version, Long sample, PreparationDetailsGet input) {
        super(PREPARATION_GROUP, input);
        this.id = id;
        this.version = version;
        this.sample = sample;
        execute(this::onExecute);
        on(HttpStatus.OK).then(pipeStream());
    }


    private HttpRequestBase onExecute() {

        // get the preparation to extract the dataset id (DataSetId should be sent from the frontend instead)
        final Preparation preparation;
        try (InputStream details = getInput()) {
            preparation = objectMapper.readerFor(Preparation.class).readValue(details);
        } catch (IOException e) {
            throw new TDPException(UNABLE_TO_READ_PREPARATION, e, build().put("id", id));
        }

        String datasetId = preparation.getDataSetId();

        String uri = transformationServiceUrl + "/apply/preparation/" + id + "/dataset/" + datasetId + "/JSON";
        uri += "?stepId=" + version;
        if (sample != null) {
            uri += "&sample=" + sample;
        }
        return new HttpGet(uri);
    }

}
