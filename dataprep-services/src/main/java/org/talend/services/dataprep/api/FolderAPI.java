// ============================================================================
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

package org.talend.services.dataprep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.util.SortAndOrderHelper;

import io.swagger.annotations.ApiOperation;

@Service(name = "dataprep.FolderAPI")
public interface FolderAPI {

    /**
     * List children folders of the parameter if null list root children.
     *
     * @param parentId
     * @return List children folders of the parameter if null list root children.
     */
    @RequestMapping(value = "/api/folders", method = GET)
    @Timed
    ResponseEntity<StreamingResponseBody> children(@RequestParam(required = false, name = "parentId") String parentId);

    /**
     * List all folders
     *
     * @return All folders.
     */
    @RequestMapping(value = "/api/folders/tree", method = GET)
    @Timed
    StreamingResponseBody getTree();

    /**
     * Get folder by id.
     *
     * @param id The folder id.
     * @return The folder with given id.
     */
    @RequestMapping(value = "/api/folders/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody getFolderAndHierarchyById(@PathVariable(value = "id") String id);

    /**
     *
     * @param parentId
     * @param path
     * @return
     */
    @RequestMapping(value = "/api/folders", method = PUT)
    @ApiOperation(value = "Add a folder.", produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody addFolder(@RequestParam(name = "parentId") String parentId, @RequestParam(name = "path") String path);

    /**
     * Remove a Folder
     *
     * @param id
     */
    @RequestMapping(value = "/api/folders/{id}", method = DELETE)
    @ApiOperation(value = "")
    @Timed
    ResponseEntity<String> removeFolder(@PathVariable("id") String id);

    /**
     * Rename a Folder
     *
     * @param id
     * @param newName
     */
    @RequestMapping(value = "/api/folders/{id}/name", method = PUT)
    @Timed
    void renameFolder(@PathVariable("id") String id, @RequestBody String newName);

    /**
     * Search Folders with parameter as part of the name
     *
     * @param name The folder to search.
     * @param strict Strict mode means searched name is the full name.
     * @return the list of folders that match the given name.
     */
    @RequestMapping(value = "/api/folders/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<StreamingResponseBody> search(@RequestParam(name = "name") String name,
            @RequestParam(required = false, name = "strict") boolean strict);

    /**
     * List all the folders and preparations out of the given id.
     *
     * @param id Where to list folders and preparations.
     */
    @RequestMapping(value = "/api/folders/{id}/preparations", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody listPreparationsByFolder(@PathVariable("id") String id, //
            @RequestParam(defaultValue = "creationDate", name = "sort") SortAndOrderHelper.Sort sort, //
            @RequestParam(defaultValue = "desc", name = "order") SortAndOrderHelper.Order order);
}
