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

package org.talend.dataprep.dataset.service;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.dataset.DataSetBaseTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;

/**
 * Unit/integration tests for the FolderService
 */
public class FolderServiceTest extends DataSetBaseTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldListChildren() throws Exception {
        // given
        createFolder("foo");
        createFolder("foo/bar");
        createFolder("foo/toto");

        // when
        final String json = given().queryParam("path", "foo") //
                .expect().statusCode(200).log().ifError()//
                .when() //
                .get("/folders").asString();

        //then
        final List<Folder> folders = mapper.readValue(json, new TypeReference<List<Folder>>(){});
        assertThat(folders.size(), is(2));
        assertThat(folders.get(0).getName(), is("bar"));
        assertThat(folders.get(1).getName(), is("toto"));
    }

    @Test
    public void shouldNotFindFolder() throws Exception {
        // when
        final Response response = given().queryParam("path", "should/not/be/found") //
                .expect().statusCode(404).log().ifError()//
                .when() //
                .get("/folders");

        // then
        assertThat(response.getStatusCode(), is(404));
    }

    private void createFolder(String path) {
        given().queryParam("path", path) //
                .expect().statusCode(200).log().ifError()//
                .when() //
                .put("/folders").then().assertThat().statusCode(200);
    }
}
