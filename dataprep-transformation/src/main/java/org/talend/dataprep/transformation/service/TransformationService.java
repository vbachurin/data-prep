package org.talend.dataprep.transformation.service;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.transformation.aggregation.AggregationService;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngine;
import org.talend.dataprep.transformation.format.ExportFormat;
import org.talend.dataprep.transformation.format.FormatRegistrationService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "transformations", basePath = "/transform", description = "Deprecated transformations on data")
public class TransformationService {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(TransformationService.class);

    /** The Spring application context. */
    @Autowired
    private ApplicationContext context;

    /** The dataprep ready to use jackson object builder. */
    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    /** All available transformation actions. */
    @Autowired
    private ActionMetadata[] allActions;

    /** The transformer factory. */
    @Autowired
    private TransformerFactory factory;

    /** he aggregation service. */
    @Autowired
    private AggregationService aggregationService;

    /** The format registration service. */
    @Autowired
    private FormatRegistrationService formatRegistrationService;

    /** The action suggestion engine. */
    @Autowired
    private SuggestionEngine suggestionEngine;

    /** DataSet service url. */
    @Value("${dataset.service.url}")
    private String datasetServiceUrl;

    /** Preparation service url. */
    @Value("${preparation.service.url}")
    protected String preparationServiceUrl;

    /** Http client used to retrieve datasets or preparations. */
    @Autowired
    private HttpClient httpClient;

    /**
     * Apply all <code>actions</code> to <code>content</code>. Actions is a Base64-encoded JSON list of
     * {@link ActionMetadata} with parameters.
     * <p>
     * To prevent the actions to exceed URL length limit, everything is shipped within via the multipart request body.
     * AggregationOperation allows client to customize the output format (see {@link ExportFormat available format
     * types} ).
     * <p>
     * To prevent the actions to exceed URL length limit, everything is shipped within via the multipart request body.
     *
     * @param formatName The output {@link ExportFormat format}. This format also set the MIME response type.
     * @param actions A Base64-encoded list of actions.
     * @param content A JSON input that complies with {@link DataSet} bean.
     * @param response The response used to send transformation result back to client.
     */
    @RequestMapping(value = "/transform/{format}", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Export the preparation applying the transformation", notes = "This operation format the input data transformed using the supplied actions in the provided format.", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @VolumeMetered
    @Deprecated
    //@formatter:off
    public void transform(@ApiParam(value = "Output format.") @PathVariable("format") final String formatName, //
                          @ApiParam(value = "Actions to perform on content.") @RequestPart(value = "actions", required = false) final Part actions, //
                          @ApiParam(value = "Data set content as JSON.") @RequestPart(value = "content", required = false) final Part content, //
                          final HttpServletResponse response, final HttpServletRequest request) {
    //@formatter:on
        final ExportFormat format = formatRegistrationService.getByName(formatName);
        if (format == null) {
            LOG.error("Export format {} not supported", formatName);
            throw new TDPException(TransformationErrorCodes.OUTPUT_TYPE_NOT_SUPPORTED);
        }

        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(content.getInputStream())) {
            Map<String, Object> arguments = new HashMap<>();
            final Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {

                final String paramName = names.nextElement();

                // filter out the content and the actions
                if (StringUtils.equals("actions", paramName) || StringUtils.equals("content", paramName) || //
                        StringUtils.equals(ExportFormat.Parameter.FILENAME_PARAMETER, paramName)) {
                    continue;
                }

                final String paramValue = request.getParameter(paramName);
                arguments.put(paramName, paramValue);

            }
            String decodedActions = actions == null ? StringUtils.EMPTY : IOUtils.toString(actions.getInputStream());
            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);

            // set headers
            String name = request.getParameter("exportParameters." + ExportFormat.Parameter.FILENAME_PARAMETER);
            if (StringUtils.isBlank(name)) {
                name = "untitled";
            }

            response.setContentType(format.getMimeType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + name + format.getExtension() + "\"");

            Configuration configuration = Configuration.builder() //
                    .format(format.getName()) //
                    .args(arguments) //
                    .output(response.getOutputStream()) //
                    .actions(decodedActions) //
                    .build();
            factory.get(configuration).transform(dataSet, configuration);
        } catch (JsonMappingException e) {
            // Ignore (end of input)
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

}
