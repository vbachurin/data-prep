package org.talend.dataprep.dataset;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DataSetServiceTests {

    @Value("${local.server.port}")
    public int                    port;

    private RestTemplate          restTemplate = new TestRestTemplate();

    private HttpStatusInterceptor interceptor;

    @Before
    public void setUp() {
        interceptor = new HttpStatusInterceptor();
        List<ClientHttpRequestInterceptor> interceptors = Arrays.<ClientHttpRequestInterceptor> asList(interceptor);
        restTemplate.setInterceptors(interceptors);
    }

    @Test
    public void testList() {
        ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:" + port + "/datasets", Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void testCreate() {
        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + "/datasets", StringUtils.EMPTY,
                Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void testGet() {
        ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:" + port + "/datasets/123456", Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void testDelete() throws Exception {
        restTemplate.delete("http://localhost:" + port + "/datasets/123456", Void.class);
        assertThat(interceptor.getLastStatus(), equalTo(HttpStatus.OK));
    }

    @Test
    public void testUpdate() throws Exception {
        restTemplate.put("http://localhost:" + port + "/datasets/123456", Void.class);
        assertThat(interceptor.getLastStatus(), equalTo(HttpStatus.OK));
    }

    private static class HttpStatusInterceptor implements ClientHttpRequestInterceptor {

        private HttpStatus statusCode;

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            statusCode = response.getStatusCode();
            return response;
        }

        public HttpStatus getLastStatus() {
            return statusCode;
        }
    }
}
