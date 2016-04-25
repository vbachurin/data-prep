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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.talend.dataprep.exception.error.APIErrorCodes.UNABLE_TO_SEARCH_DATAPREP;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.metrics.Timed;

import com.fasterxml.jackson.core.JsonGenerator;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * API in charge of the search.
 */
@RestController
public class SearchAPI extends APIService {


    /**
     * Search dataprep folders, preparations and datasets.
     *
     * @param name the name searched.
     */
    //@formatter:off
    @RequestMapping(value = "/api/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List the inventory of elements contained in a folder matching the given name", produces = APPLICATION_JSON_VALUE)
    @Timed
    public void inventorySearch(
            @ApiParam(value = "Name") @RequestParam(defaultValue = "", required = false) String name,
            final OutputStream output) {
    //@formatter:on

        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching dataprep for '{}' (pool: {})...", name, getConnectionStats());
        }

        final int foldersFound;
        try (final JsonGenerator generator = mapper.getFactory().createGenerator(output)) {

            generator.writeStartObject();

            foldersFound = searchAndWriteFolders(name, generator);

            generator.writeEndObject();

        } catch (IOException e) {
            throw new TDPException(UNABLE_TO_SEARCH_DATAPREP, e);
        }

        LOG.info("Searching dataprep for {} done, found {} folder(s)", name, foldersFound);

    }

    /**
     * Search for the given name in the folder and write the result straight to output.
     * @param name the name searched.
     * @param output where to write the json.
     * @return the number of folders that match the searched name.
     */
    private int searchAndWriteFolders(String name, JsonGenerator output) {
        return 0;
    }

}
