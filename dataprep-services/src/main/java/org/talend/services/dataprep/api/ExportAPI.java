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

package org.talend.services.dataprep.api;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.format.export.ExportFormatMessage;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.security.PublicAPI;

@Service(name = "dataprep.ExportAPI")
public interface ExportAPI {

    /**
     * Export a dataset or a preparation to file. The file type is provided in the request body.
     *
     * @param parameters
     * @return
     */
    @RequestMapping(value = "/api/export", method = GET)
    ResponseEntity<StreamingResponseBody> export(@Valid ExportParameters parameters);

    /**
     * Get the available export formats
     */
    @RequestMapping(value = "/api/export/formats", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    Callable<Stream<ExportFormatMessage>> exportTypes();

    /**
     * Get the available export formats for preparation
     */
    @RequestMapping(value = "/api/export/formats/preparations/{preparationId}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    Callable<Stream<ExportFormatMessage>> exportTypesForPreparation(@PathVariable("preparationId") String preparationId);

    /**
     * Get the available export formats for dataset
     */
    @RequestMapping(value = "/api/export/formats/datasets/{dataSetId}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    Callable<Stream<ExportFormatMessage>> exportTypesForDataSet(@PathVariable("dataSetId") String dataSetId);
}
