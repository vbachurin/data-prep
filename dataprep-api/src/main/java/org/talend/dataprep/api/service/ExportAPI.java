package org.talend.dataprep.api.service;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.api.ExportInput;
import org.talend.dataprep.api.service.command.export.Export;
import org.talend.dataprep.api.service.validation.OneNotNull;
import org.talend.dataprep.api.type.ExportType;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import static org.talend.dataprep.api.type.ExportType.CSV;

@RestController
@Api(value = "api", basePath = "/api", description = "Export data API")
public class ExportAPI extends APIService {

    @RequestMapping(value = "/api/export", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    @ApiOperation(value = "Export a dataset", consumes = MediaType.APPLICATION_JSON_VALUE, notes = "Export a dataset or a preparation to file. The file type is provided in the request body.")
    public void export(
            @ApiParam(value = "Export configuration")
            @RequestBody
            @Valid
            final ExportInput input,
            final HttpServletResponse response) {
        try {
            final HystrixCommand<InputStream> command = getCommand(Export.class, getClient(), input, response);
            final ServletOutputStream outputStream = response.getOutputStream();

            IOUtils.copyLarge(command.execute(), outputStream);
            outputStream.flush();

        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_EXPORT_CONTENT, e);
        }
    }
}
