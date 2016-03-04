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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.exception.error.DataSetErrorCodes.FOLDER_NOT_EMPTY;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.folder.FolderContent;
import org.talend.dataprep.api.folder.FolderEntry;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.folder.store.FolderRepository;
import org.talend.dataprep.folder.store.NotEmptyFolderException;
import org.talend.dataprep.metrics.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "folders", basePath = "/folders", description = "Operations on folders")
public class FolderService {

    private FolderRepository folderRepository;

    private DataSetMetadataRepository dataSetMetadataRepository;

    @Inject
    public FolderService(FolderRepository folderRepository, DataSetMetadataRepository dataSetMetadataRepository) {
        this.folderRepository = folderRepository;
        this.dataSetMetadataRepository = dataSetMetadataRepository;
    }

    /**
     * List direct sub folders for the given path.
     *
     * @param path the current folder where to look for children.
     * @return direct sub folders for the given path.
     */
    @RequestMapping(value = "/folders", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Folder children", produces = APPLICATION_JSON_VALUE, notes = "List all child folders of the one as parameter")
    @Timed
    public Iterable<Folder> children(@RequestParam(required = false, defaultValue = "") String path) {
        if (!folderRepository.exists(path)) {
            throw new TDPException(DataSetErrorCodes.FOLDER_NOT_FOUND, build().put("path", path));
        }
        return folderRepository.children(path);
    }

    /**
     * @return All folders.
     */
    @RequestMapping(value = "/folders/all", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "All Folders", produces = APPLICATION_JSON_VALUE, notes = "List all existing folders")
    @Timed
    public Iterable<Folder> allFolder() {
        return folderRepository.allFolder();
    }

    /**
     * Search for folders.
     *
     * @param pathName the part of the name to look for.
     * @return the folders whose part of their name match the given path.
     */
    @RequestMapping(value = "/folders/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Search Folders with parameter as part of the name", produces = APPLICATION_JSON_VALUE, notes = "")
    @Timed
    public Iterable<Folder> search(@RequestParam(required = false) String pathName) {
        return folderRepository.searchFolders(pathName);
    }

    /**
     * Add a folder.
     *
     * @param path where to add the folder.
     * @return the created folder.
     */
    @RequestMapping(value = "/folders", method = PUT, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a Folder", produces = APPLICATION_JSON_VALUE, notes = "Create a folder")
    @Timed
    public Folder addFolder(@RequestParam String path) {
        return folderRepository.addFolder(path);
    }

    /**
     * Remove the folder. Throws an exception if the folder, or one of its sub folders, contains an entry.
     *
     * @param path the path that points to the folder to remove.
     */
    @RequestMapping(value = "/folders", method = DELETE)
    @ApiOperation(value = "Remove a Folder", produces = APPLICATION_JSON_VALUE, notes = "Remove the folder")
    @Timed
    public void removeFolder(@RequestParam String path) {
        try {
            folderRepository.removeFolder(path);
        } catch (NotEmptyFolderException e) {
            throw new TDPException(FOLDER_NOT_EMPTY, e);
        }
    }

    /**
     * Rename the folder to the new path.
     *
     * @param path where to look for the folder.
     * @param newPath the new folder path.
     */
    @RequestMapping(value = "/folders/rename", method = PUT)
    @ApiOperation(value = "Rename a Folder", produces = APPLICATION_JSON_VALUE)
    @Timed
    public void renameFolder(@RequestParam String path, @RequestParam String newPath) {
        folderRepository.renameFolder(path, newPath);
    }

    /**
     * Remove a folder entry.
     *
     * @param path where to look for the entry.
     * @param contentId the content id.
     * @param contentType the entry content type.
     */
    @RequestMapping(value = "/folders/entries/{contentType}/{id}", method = DELETE)
    @ApiOperation(value = "Remove a FolderEntry", notes = "Delete the folder entry")
    @Timed
    public void deleteFolderEntry(@RequestParam String path, @PathVariable(value = "id") String contentId, //
            @PathVariable(value = "contentType") String contentType) {
        try {
            FolderEntry.ContentType checkedContentType = FolderEntry.ContentType.get(contentType);
            folderRepository.removeFolderEntry(path, contentId, checkedContentType);
        } catch (IllegalArgumentException exc) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_REMOVE_FOLDER_ENTRY, exc);
        }

    }

    /**
     * Return the list of folder entries out of the given path.
     *
     * @param path the path where to look for entries.
     * @param contentType the type of wanted entries.
     * @return the list of folder entries out of the given path.
     */
    @RequestMapping(value = "/folders/entries", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List folder entries", produces = APPLICATION_JSON_VALUE, notes = "List all folder entries of the given content type")
    @Timed
    public Iterable<FolderEntry> entries(@RequestParam String path, @RequestParam String contentType) {
        try {
            FolderEntry.ContentType checkedContentType = FolderEntry.ContentType.get(contentType);
            return folderRepository.entries(path, checkedContentType);
        } catch (IllegalArgumentException exc) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_LIST_FOLDER_ENTRIES, exc);
        }

    }

    /**
     * Return the inventory of elements contained in a folder (which can be data sets, preparation and folders) out of
     * the given path.
     *
     * @param path the path where to look for folder elements.
     * @param name the string that must be included in the name of the folder element
     * @return the inventory of elements contained if the folder corresponding to the given path matching the given name.
     */
    @RequestMapping(value = "/inventory/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List the inventory of elements contained in a folder matching the given name", produces = APPLICATION_JSON_VALUE, notes = "List the inventory of elements contained in a folder matching the given name")
    @Timed
    public FolderContent inventorySearch(@RequestParam(defaultValue = "", required = false) String path,
            @RequestParam(defaultValue = "", required = true) String name) {

        FolderContent result = new FolderContent();
        Iterable<Folder> folderIterable = folderRepository.allFolder();

        List<Folder> folders = StreamSupport.stream(folderIterable.spliterator(), false)
                .filter(f -> f.getPath().startsWith(path)).collect(Collectors.toList());

        Set<String> contentIds = new HashSet<>();

        // retrieve elements contained in sub folders of path
        for (Folder folder : folders) {
            Set<String> entries = StreamSupport
                    .stream(folderRepository.entries(folder.getPath(), FolderEntry.ContentType.DATASET).spliterator(), false)
                    .map(f -> f.getContentId()).collect(Collectors.toSet());
            contentIds.addAll(entries);
        }

        // retrieve data sets contained in pathSet<String> entries = StreamSupport
        Set<String> entries = StreamSupport
                .stream(folderRepository.entries(path, FolderEntry.ContentType.DATASET).spliterator(), false)
                .map(f -> f.getContentId()).collect(Collectors.toSet());
        contentIds.addAll(entries);

        // retrieve the data sets metadata from their ids and filter on matching name
        List<DataSetMetadata> datasets = contentIds.stream().map(s -> dataSetMetadataRepository.get(s))
                .filter(d -> d != null && d.getName() != null && d.getName().contains(name)).collect(Collectors.toList());

        // filter folders by name
        List<Folder> folderList = StreamSupport.stream(folders.spliterator(), false).filter(f -> f.getName().contains(name))
                .collect(Collectors.toList());
        result.setFolders(folderList);
        result.setDatasets(datasets);
        return result;
    }

}
