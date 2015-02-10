package org.talend.dataprep.transformation.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.transformation.api.transformer.SimpleTransformerFactory;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "transformations", basePath = "/transform", description = "Transformations on data")
public class TransformationService {

    private TransformerFactory factory = new SimpleTransformerFactory();

    @RequestMapping(value = "/transform", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Transform input data", notes = "This service return the input data transformed using the supplied actions.")
    @VolumeMetered
    public void transform(
            @ApiParam(value = "Actions to perform on content (encoded in Base64).") @RequestParam(value = "actions", defaultValue = "", required = false) String actions,
            @ApiParam(value = "Data set content as JSON") InputStream content, HttpServletResponse response) {
        try {
            Transformer transformer = factory.get(new String(Base64.getDecoder().decode(actions)));
            transformer.transform(content, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to process JSON input", e);
        }
    }

}
