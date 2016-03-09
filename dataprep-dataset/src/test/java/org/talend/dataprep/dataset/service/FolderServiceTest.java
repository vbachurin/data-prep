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
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.dataset.DataSetBaseTest;
import org.talend.dataprep.folder.store.FolderRepository;
import org.talend.dataprep.inventory.Inventory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;

/**
 * Unit/integration tests for the FolderService
 */
public class FolderServiceTest extends DataSetBaseTest {

    @Autowired
    private FolderRepository folderRepository;

    @After
    public void clear() {
        folderRepository.clear();
    }

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

        // then
        final List<Folder> folders = mapper.readValue(json, new TypeReference<List<Folder>>() {
        });
        final List<String> foldersNames = folders.stream().map(Folder::getName).collect(toList());
        assertThat(foldersNames.size(), is(2));
        assertThat(foldersNames, containsInAnyOrder("bar", "toto"));
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

    private void removeThenCreateFolder(String path) {

        given().queryParam("path", path) //
                .when() //
                .delete("/folders");

        given().queryParam("path", path) //
                .expect().statusCode(200).log().ifError()//
                .when() //
                .put("/folders").then().assertThat().statusCode(200);
    }

    private String createFolderEntry(String path, String name) throws IOException {

        return given().body(IOUtils.toString(this.getClass().getResourceAsStream(""))).queryParam("Content-Type", "text/csv")
                .queryParam("folderPath", path).queryParam("name", name).when().post("/datasets").asString();

    }

    @Test
    public void shouldSearchInventory() throws Exception {
        // given
        createFolder("foo");
        createFolder("foo/bar");
        createFolder("foo/toto");
        createFolder("yoyo/bar/bari");
        createFolderEntry("foo/toto", "bar");

        // when
        final String json = given().queryParam("path", "foo").queryParam("name", "bar") //
                .expect().statusCode(200).log().ifError()//
                .when() //
                .get("/inventory/search").asString();

        // then
        Inventory inventory = mapper.readValue(json, new TypeReference<Inventory>() {
        });
        assertThat(inventory.getFolders().size(), is(1));
        assertThat(inventory.getDatasets().size(), is(1));
        assertThat(inventory.getPreparations().size(), is(0));
        assertThat(inventory.getFolders().get(0).getPath(), is("foo/bar"));
        assertThat(inventory.getDatasets().get(0).getMetadata().getName(), is("bar"));
    }

}
