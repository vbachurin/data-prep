package org.talend.dataprep.api.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.service.api.ExportParameters;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.api.service.command.export.Export;
import org.talend.dataprep.api.service.command.export.ExportTypes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.metrics.Timed;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "api", basePath = "/api", description = "Export data API")
public class ExportAPI extends APIService {

    @RequestMapping(value = "/api/export", method = GET)
    @ApiOperation(value = "Export a dataset", consumes = APPLICATION_FORM_URLENCODED_VALUE, notes = "Export a dataset or a preparation to file. The file type is provided in the request body.")
    public void export(@ApiParam(value = "Export configuration") @Valid final ExportParameters input, //
                       final HttpServletResponse response, //
                       final HttpServletRequest request) {
        try {
            Map<String, String> arguments = new HashMap<>();
            final Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                final String paramName = names.nextElement();
                if (StringUtils.contains(paramName, "exportParameters.")) {
                    final String paramValue = request.getParameter(paramName);
                    arguments.put(paramName, StringUtils.isNotEmpty(paramValue)? paramValue : StringUtils.EMPTY);

                }
            }
            input.setArguments(arguments);
            final GenericCommand<InputStream> command = getCommand(Export.class, getClient(), input, response);
            final ServletOutputStream outputStream = response.getOutputStream();
            final InputStream commandInputStream = command.execute();

            // copy all headers from the command response so that the mime-type is correctly forwarded for instance
            final Header[] commandResponseHeaders = command.getCommandResponseHeaders();
            for (Header header : commandResponseHeaders) {
                response.setHeader(header.getName(), header.getValue());
            }
            IOUtils.copyLarge(commandInputStream, outputStream);
            outputStream.flush();
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
    public void exportTypes(final HttpServletResponse response) {
        try {
            final HystrixCommand<InputStream> command = getCommand(ExportTypes.class, getClient());
            final ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copyLarge(command.execute(), outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_EXPORT_CONTENT, e);
        }
    }
}
