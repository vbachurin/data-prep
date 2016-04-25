//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.api.service;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.jayway.restassured.response.Response;

/**
 *
 */
public class SearchAPITest extends ApiServiceTestBase {

    @Test
    public void shouldSearch() throws Exception {

        // given
        folderRepository.addFolder("/beer");
        folderRepository.addFolder("/beer/Queue de charrue");
        folderRepository.addFolder("/beer/Saint Feuillien");

        folderRepository.addFolder("/whisky");
        folderRepository.addFolder("/whisky/McCallan Sherry Oak");
        folderRepository.addFolder("/whisky/McCallan Fine Oak");
        folderRepository.addFolder("/whisky/McCallan 1824 Collection");

        // when
        final Response response = given() //
                .queryParam("name", "callan") //
                .when()//
                .expect().statusCode(200).log().ifError() //
                .get("/api/search");

        // then
        assertThat(response.getStatusCode(), is(200));
        System.out.println(response.asString());
    }
}