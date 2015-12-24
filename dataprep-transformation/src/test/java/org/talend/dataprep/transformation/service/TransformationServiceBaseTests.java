package org.talend.dataprep.transformation.service;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.talend.dataprep.transformation.TransformationBaseTest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * Base class for TransformationService integration tests.
 */
public abstract class TransformationServiceBaseTests extends TransformationBaseTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(TransformationServiceBaseTests.class);

    @Value("${local.server.port}")
    protected int port;

    /** Needed to update its url at runtime. */
    @Autowired
    private TransformationService transformationService;

    @Before
    public void setUp() {

        RestAssured.port = port;

        ReflectionTestUtils.setField( //
                unwrapProxy(TransformationService.class, transformationService), // voodoo ?
                "datasetServiceUrl", //
                "http://localhost:" + port, //
                String.class);

        ReflectionTestUtils.setField( //
                unwrapProxy(TransformationService.class, transformationService), // black magic ?
                "preparationServiceUrl", //
                "http://localhost:" + port, //
                String.class);
    }

    /**
     * Black magic / voodoo needed to make ReflectionTestUtils.setField(...) work on class proxied by Spring.
     *
     * @param clazz the wanted class.
     * @param proxy the proxy.
     * @return the actual object behind the proxy.
     */
    private Object unwrapProxy(Class clazz, Object proxy) {
        if (AopUtils.isAopProxy(proxy) && proxy instanceof Advised) {
            try {
                Object target = ((Advised) proxy).getTargetSource().getTarget();
                return target;
            } catch (Exception e) {
            }
        }
        return proxy;
    }

    protected String createDataset(final String file, final String name, final String type) throws IOException {
        return createDataset(file, name, type, null);
    }

    protected String createDataset(final String file, final String name, final String type, final String folderPath)
            throws IOException {
        final String datasetContent = IOUtils.toString(this.getClass().getResourceAsStream(file));
        final Response post = given() //
                .contentType(ContentType.JSON) //
                .body(datasetContent) //
                .queryParam("Content-Type", type) //
                .when() //
                .post("/datasets?name={name}&folderPath={folderPath}", name, folderPath);

        final int statusCode = post.getStatusCode();
        if (statusCode != 200) {
            LOGGER.error("Unable to create dataset (HTTP " + statusCode + "). Error: {}", post.asString());
        }
        assertThat(statusCode, is(200));
        final String dataSetId = post.asString();
        assertNotNull(dataSetId);
        assertThat(dataSetId, not(""));

        return dataSetId;
    }

    protected String createEmptyPreparationFromFile(final String file, final String name, final String type) throws IOException {
        final String dataSetId = createDataset(file, "testDataset", type);
        return createEmptyPreparationFromDataset(dataSetId, name);
    }

    protected String createEmptyPreparationFromDataset(final String dataSetId, final String name) throws IOException {
        final Response post = given().contentType(ContentType.JSON)
                .body("{ \"name\": \"" + name + "\", \"dataSetId\": \"" + dataSetId + "\"}").when().put("/preparations");

        if (post.getStatusCode() != 200) {
            LOGGER.error("Unable to create preparation (HTTP " + post.getStatusCode() + "). Error: {}", post.asString());
        }

        assertThat(post.getStatusCode(), is(200));

        final String preparationId = post.getBody().asString();
        assertThat(preparationId, notNullValue());
        assertThat(preparationId, not(""));

        return preparationId;
    }

    protected void applyActionFromFile(final String preparationId, final String actionFile) throws IOException {
        final String action = IOUtils.toString(this.getClass().getResourceAsStream(actionFile));
        applyAction(preparationId, action);
    }

    protected void applyAction(final String preparationId, final String action) throws IOException {
        given().contentType(ContentType.JSON) //
                .body(action) //
                .when() //
                .post("/preparations/{id}/actions", preparationId) //
                .then() //
                .statusCode(is(200));
    }
}
