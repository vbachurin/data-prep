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

package org.talend.dataprep.api.service;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.exception.error.CommonErrorCodes.UNABLE_TO_WRITE_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.api.EnrichedPreparation;
import org.talend.dataprep.api.service.command.common.HttpResponse;
import org.talend.dataprep.api.service.command.dataset.DataSetGetMetadata;
import org.talend.dataprep.api.service.command.folder.*;
import org.talend.dataprep.api.service.command.preparation.PreparationListByFolder;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.http.HttpResponseContext;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.netflix.hystrix.HystrixCommand;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class FolderAPI extends APIService {

    @RequestMapping(value = "/api/folders", method = GET)
    @ApiOperation(value = "List children folders of the parameter if null list root children.", produces = APPLICATION_JSON_VALUE)
    @Timed
    public void children(@RequestParam final String parentId, final OutputStream output) {
        final HystrixCommand<InputStream> foldersList = getCommand(FolderChildrenList.class, parentId);
        try (InputStream commandResult = foldersList.execute()){
            HttpResponseContext.header("Content-Type", APPLICATION_JSON_VALUE); //$NON-NLS-1$
            IOUtils.copyLarge(commandResult, output);
            output.flush();
        } catch (IOException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDERS, e);
        }
    }

    @RequestMapping(value = "/api/folders/tree", method = GET)
    @ApiOperation(value = "List all folders", produces = APPLICATION_JSON_VALUE)
    @Timed
    public void getTree(final OutputStream output) {
        final HystrixCommand<InputStream> foldersList = getCommand(FolderTree.class);
        try (InputStream commandResult = foldersList.execute()){
            HttpResponseContext.header("Content-Type", APPLICATION_JSON_VALUE); //$NON-NLS-1$
            IOUtils.copyLarge(commandResult, output);
            output.flush();
        } catch (IOException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDERS, e);
        }
    }

    @RequestMapping(value = "/api/folders/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get folder by id", produces = APPLICATION_JSON_VALUE, notes = "Get a folder by id")
    @Timed
    public void getFolderAndHierarchyById(@PathVariable(value = "id") final String id, final OutputStream output) {
        final HystrixCommand<InputStream> foldersList = getCommand(GetFolder.class, id);
        try (InputStream commandResult = foldersList.execute()){
            HttpResponseContext.header("Content-Type", APPLICATION_JSON_VALUE); //$NON-NLS-1$
            IOUtils.copyLarge(commandResult, output);
            output.flush();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_GET_FOLDERS, e);
        }
    }

    @RequestMapping(value = "/api/folders", method = PUT)
    @ApiOperation(value = "Add a folder.", produces = APPLICATION_JSON_VALUE)
    @Timed
    public void addFolder(@RequestParam final String parentId, @RequestParam final String path, final OutputStream output) {
        final HystrixCommand<InputStream> createChildFolder = getCommand(CreateChildFolder.class, parentId, path);
        try (InputStream commandResult = createChildFolder.execute()){
            HttpResponseContext.header("Content-Type", APPLICATION_JSON_VALUE); //$NON-NLS-1$
            IOUtils.copyLarge(commandResult, output);
            output.flush();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_FOLDER, e);
        }
    }

    @RequestMapping(value = "/api/folders/{id}", method = DELETE)
    @ApiOperation(value = "Remove a Folder")
    @Timed
    public void removeFolder(@PathVariable final String id, final OutputStream output) {
        try {
            final HystrixCommand<HttpResponse> removeFolder = getCommand(RemoveFolder.class, id);
            final HttpResponse result = removeFolder.execute();
            try {
                HttpResponseContext.status(HttpStatus.valueOf(result.getStatusCode()));
                HttpResponseContext.header("Content-Type", result.getContentType());
                IOUtils.write(result.getHttpContent(), output);
                output.flush();
            } catch (IOException e) {
                throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }

        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_DELETE_FOLDER, e);
        }
    }

    @RequestMapping(value = "/api/folders/{id}/name", method = PUT)
    @ApiOperation(value = "Rename a Folder")
    @Timed
    public void renameFolder(@PathVariable final String id, @RequestBody final String newName) {

        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(newName)) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_RENAME_FOLDER);
        }

        try {
            final HystrixCommand<Void> renameFolder = getCommand(RenameFolder.class, id, newName);
            renameFolder.execute();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_RENAME_FOLDER, e);
        }
    }


    /**
     * no javadoc here so see description in @ApiOperation notes.
     *
     * @param name The folder to search.
     * @param strict Strict mode means searched name is the full name.
     * @return
     */
    @RequestMapping(value = "/api/folders/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Search Folders with parameter as part of the name", produces = APPLICATION_JSON_VALUE)
    @Timed
    public void search(@RequestParam final String name,
                       @RequestParam(required = false) final boolean strict,
                       final OutputStream output) {
        final HystrixCommand<InputStream> searchFolders = getCommand(SearchFolders.class, name, strict);
        try (InputStream commandResult = searchFolders.execute()){
            HttpResponseContext.header("Content-Type", APPLICATION_JSON_VALUE); //$NON-NLS-1$
            IOUtils.copyLarge(commandResult, output);
            output.flush();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDERS, e);
        }
    }


    /**
     * TODO This is not used : should remove ?
     * The folder entry deletion is directly done in DELETE /preparations/{id}
     */
    @RequestMapping(value = "/api/folders/entries/{contentType}/{id}", method = DELETE)
    @ApiOperation(value = "Remove a Folder Entry")
    @Timed
    @Deprecated
    public void deleteFolderEntry( //
            @PathVariable(value = "contentType") final String contentType, //
            @PathVariable(value = "id") final String contentId, //
            @RequestParam final String path) {
        try {
            final HystrixCommand<Void> removeFolderEntry = getCommand(RemoveFolderEntry.class, path, contentType,
                    contentId);
            removeFolderEntry.execute();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_DELETE_FOLDER_ENTRY, e);
        }
    }


    /**
     * TODO Vincent check if really used or not
     */
    @RequestMapping(value = "/api/folders/entries", method = GET)
    @ApiOperation(value = "List all folder entries of the given content type within the path", produces = APPLICATION_JSON_VALUE)
    @Timed
    @VolumeMetered
    @Deprecated
    public void entries(@RequestParam String path, @RequestParam String contentType, final OutputStream output) {
        final HystrixCommand<InputStream> listFolderEntries = getCommand(FolderEntriesList.class, path,
                contentType);
        try (InputStream commandResult = listFolderEntries.execute()) {
            HttpResponseContext.header("Content-Type", APPLICATION_JSON_VALUE); //$NON-NLS-1$
            IOUtils.copyLarge(commandResult, output);
            output.flush();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDER_ENTRIES, e);
        }
    }


    /**
     * List all the folders and preparations out of the given id.
     *
     * @param id Where to list folders and preparations.
     * @param output the http response.
     */
    //@formatter:off
    @RequestMapping(value = "/api/folders/{id}/preparations", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all preparations for a given id.", notes = "Returns the list of preparations for the given id the current user is allowed to see.")
    @Timed
    public void listPreparationsByFolder(
            @PathVariable @ApiParam(name = "id", value = "The destination to search preparations from.") final String id, //
            @ApiParam(value = "Sort key (by name or date), defaults to 'date'.") @RequestParam(defaultValue = "DATE") final String sort, //
            @ApiParam(value = "Order for sort key (desc or asc), defaults to 'desc'.") @RequestParam(defaultValue = "DESC") final String order, //
            final OutputStream output) {
    //@formatter:on

        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing preparations in destination {} (pool: {} )...", id, getConnectionStats());
        }

        int preparationsProcessed;
        try (final JsonGenerator generator = mapper.getFactory().createGenerator(output)) {

            generator.writeStartObject();

            listAndWriteFoldersToJson(id, sort, order, generator);
            preparationsProcessed = listAndWritePreparationsToJson(id, sort, order, generator);

            generator.writeEndObject();

        }
        catch (IOException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDER_ENTRIES, e, build().put("destination", id));
        }

        LOG.info("There are {} preparation(s) in '{}'", preparationsProcessed, id);

    }

    /**
     * List preparations from a destination and write them straight in json to the output.
     *
     * @param folder the destination to list the preparations.
     * @param sort how to sort the preparations.
     * @param order the order to apply to the sort.
     * @param output where to write the json.
     * @throws IOException if an error occurs.
     */
    private int listAndWritePreparationsToJson(final String folder, final String sort, final String order, final JsonGenerator output) throws IOException {

        output.writeRaw(",");
        final PreparationListByFolder listPreparations = getCommand(PreparationListByFolder.class, folder, sort, order);
        try (InputStream input = listPreparations.execute()) {

            output.writeArrayFieldStart("preparations");
            final List<Preparation> preparations = mapper.readValue(input, new TypeReference<List<Preparation>>(){});
            for (Preparation preparation : preparations) {
                enrichAndWritePreparation(preparation, output);
            }
            output.writeEndArray();
            return preparations.size();
        }
        catch(Exception e) {
            throw new TDPException(UNABLE_TO_WRITE_JSON, e);
        }
    }

    /**
     * Enrich preparation with dataset information and write it in json to the output.
     *
     * @param preparation the preparation to enrich.
     * @param output where to write the json.
     */
    private void enrichAndWritePreparation(final Preparation preparation, final JsonGenerator output) {

        final DataSetGetMetadata getMetadata = getCommand(DataSetGetMetadata.class, preparation.getDataSetId());
        final EnrichedPreparation enrichedPreparation = new EnrichedPreparation(preparation, getMetadata.execute());

        try {
            output.writeObject(enrichedPreparation);
        } catch (IOException e) {
            //simply log the error as there may be other preparations that could be processed
            LOG.error("error writing {} to the http response", enrichedPreparation, e);
        }
    }

    /**
     * Get and write, in json, the list of folders directly to the output.
     *
     * @param folderId the destination to list.
     * @param sort how to sort the preparations.
     * @param order the order to apply to the sort.
     * @param output where to write the json.
     * @throws IOException if an error occurs.
     */
    private void listAndWriteFoldersToJson(final String folderId, final String sort, final String order, final JsonGenerator output) throws IOException {
        final FolderChildrenList commandListFolders = getCommand(FolderChildrenList.class, folderId, sort, order);
        try (InputStream folders = commandListFolders.execute()) {
            output.writeRaw("\"folders\":");
            output.writeRaw(IOUtils.toString(folders));
        }
    }
}
