package org.talend.dataprep.api.dataset.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.httpclient.URIException;
import org.junit.Test;

/**
 * Unit test for the HttpLocation
 * @see HttpLocation
 */
public class HttpLocationTest {

    @Test
    public void typeShouldBeHTTP() throws URIException {
        HttpLocation location = new HttpLocation();
        assertEquals("http", location.getLocationType());
    }

    @Test
    public void shouldEncodeUrl() throws URIException {
        HttpLocation location = new HttpLocation();
        location.setUrl("http://localhost:8080/unit tests/communes de france.csv");
        assertEquals("http://localhost:8080/unit%20tests/communes%20de%20france.csv", location.getUrl());
    }

    @Test
    public void shouldNotEncodeUrl() throws URIException {
        HttpLocation location = new HttpLocation();
        location.setUrl(null);
        assertNull(location.getUrl());
    }
}