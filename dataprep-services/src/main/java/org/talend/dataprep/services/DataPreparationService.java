package org.talend.dataprep.services;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "services", basePath = "/transform", description = "Transformations on data")
public class DataPreparationService {

    @RequestMapping(value = "/transform", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void transform(
            @ApiParam(value = "Actions to perform on data set") @RequestParam(value = "actions", defaultValue = "", required = false) String actions,
            @ApiParam(value = "Data set id") String dataSetId, HttpServletResponse response) {

    }

}
