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

package org.talend.dataprep.api.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.InputStream;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.dataprep.api.org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.service.command.export.Export;
import org.talend.dataprep.api.service.command.export.ExportTypes;
import org.talend.dataprep.command.CommandHelper;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.security.PublicAPI;

import com.netflix.hystrix.HystrixCommand;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ExportAPI extends APIService {

    @RequestMapping(value = "/api/export", method = GET)
    @ApiOperation(value = "Export a dataset", consumes = APPLICATION_FORM_URLENCODED_VALUE, notes = "Export a dataset or a preparation to file. The file type is provided in the request body.")
    public StreamingResponseBody export(@ApiParam(value = "Export configuration") final ExportParameters parameters, BindingResult result) {
        final List<ObjectError> allErrors = result.getAllErrors();
        for (ObjectError allError : allErrors) {
            System.out.println("allError = " + allError);
        }
        try {
            final GenericCommand<InputStream> command = getCommand(Export.class, parameters);
            return CommandHelper.toStreaming(command);
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_EXPORT_CONTENT, e);
        }
    }

    /**
     * Get the available export formats
     */
    @RequestMapping(value = "/api/export/formats", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the available format types")
    @Timed
    @PublicAPI
    public StreamingResponseBody exportTypes() {
        final HystrixCommand<InputStream> command = getCommand(ExportTypes.class);
        return CommandHelper.toStreaming(command);
    }
}
