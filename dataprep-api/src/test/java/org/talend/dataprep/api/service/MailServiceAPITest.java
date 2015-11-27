package org.talend.dataprep.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.talend.dataprep.api.service.mail.MailDetails;

public class MailServiceAPITest extends ApiServiceTestBase {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test public void shouldReturnInternalSeverError500() throws Exception {

        MailDetails mailDetails = new MailDetails();

        // send with bad recipients
        Response response = RestAssured.given() //
                .body(objectMapper.writer().writeValueAsBytes(mailDetails))//
                .contentType(ContentType.JSON) //
                .when() //
                .put("/api/mail");

        Assertions.assertThat(response.getStatusCode()).isEqualTo(400);

    }

}
