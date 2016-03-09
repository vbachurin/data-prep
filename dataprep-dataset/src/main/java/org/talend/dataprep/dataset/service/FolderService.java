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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.folder.FolderEntry;
import org.talend.dataprep.api.user.UserData;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.folder.store.FolderRepository;
import org.talend.dataprep.folder.store.NotEmptyFolderException;
import org.talend.dataprep.inventory.Inventory;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.security.Security;
import org.talend.dataprep.user.store.UserDataRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "folders", basePath = "/folders", description = "Operations on folders")
public class FolderService {

    private FolderRepository folderRepository;

    private InventoryUtils inventoryUtils;

    /** User repository. */
    private UserDataRepository userDataRepository;

    /** Dataset metadata repository. */
    private DataSetMetadataRepository dataSetMetadataRepository;

    /** DataPrep abstraction to the underlying security (whether it's enabled or not). */
    private Security security;

    @Inject
    public FolderService(FolderRepository folderRepository, InventoryUtils inventoryUtils, Security security,
            UserDataRepository userDataRepository, DataSetMetadataRepository dataSetMetadataRepository) {
        this.folderRepository = folderRepository;
        this.inventoryUtils = inventoryUtils;
        this.security = security;
        this.userDataRepository = userDataRepository;
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
     * This gets the current user data related to the dataSetMetadata and updates the dataSetMetadata accordingly. First
     * check for favorites dataset
     *
     * @param dataSetMetadata, the metadata to be updated
     */
    void completeWithUserData(DataSetMetadata dataSetMetadata) {
        String userId = security.getUserId();
        UserData userData = userDataRepository.get(userId);
        if (userData != null) {
            dataSetMetadata.setFavorite(userData.getFavoritesDatasets().contains(dataSetMetadata.getId()));
        } // no user data related to the current user to do nothing
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

    @RequestMapping(value = "/folders/datasets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all data sets", notes = "Returns the list of data sets the current user is allowed to see. Creation date is a Epoch time value (in UTC time zone).")
    @Timed
    public Inventory folderContents(
            @ApiParam(value = "Sort key (by name or date).") @RequestParam(defaultValue = "DATE", required = false) String sort,
            @ApiParam(value = "Order for sort key (desc or asc).") @RequestParam(defaultValue = "DESC", required = false) String order,
            @ApiParam(value = "Folder id to search datasets") @RequestParam(defaultValue = "", required = false) String folder) {

        Spliterator<DataSetMetadata> iterator;
        if (StringUtils.isNotEmpty(folder)) {
            Iterable<FolderEntry> entries = folderRepository.entries(folder, FolderEntry.ContentType.DATASET);
            final List<DataSetMetadata> metadatas = new ArrayList<>();
            entries.forEach(folderEntry -> {
                DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(folderEntry.getContentId());
                if (dataSetMetadata != null) {
                    metadatas.add(dataSetMetadata);
                } else {
                    folderRepository.removeFolderEntry(folderEntry.getFolderId(), //
                            folderEntry.getContentId(), //
                            folderEntry.getContentType());
                }
            });
            iterator = metadatas.spliterator();
        } else {
            iterator = dataSetMetadataRepository.list().spliterator();
        }

        Stream<DataSetMetadata> stream = StreamSupport.stream(iterator, false);
        // Select order (asc or desc)
        final Comparator<String> comparisonOrder;
        switch (order.toUpperCase()) {
        case "ASC":
            comparisonOrder = Comparator.naturalOrder();
            break;
        case "DESC":
            comparisonOrder = Comparator.reverseOrder();
            break;
        default:
            throw new TDPException(DataSetErrorCodes.ILLEGAL_ORDER_FOR_LIST, ExceptionContext.build().put("order", order));
        }
        // Select comparator for sort (either by name or date)
        final Comparator<DataSetMetadata> comparator;
        switch (sort.toUpperCase()) {
        case "NAME":
            comparator = Comparator.comparing(dataSetMetadata -> dataSetMetadata.getName().toUpperCase(), comparisonOrder);
            break;
        case "DATE":
            comparator = Comparator.comparing(dataSetMetadata -> String.valueOf(dataSetMetadata.getCreationDate()),
                    comparisonOrder);
            break;
        default:
            throw new TDPException(DataSetErrorCodes.ILLEGAL_SORT_FOR_LIST, ExceptionContext.build().put("sort", order));
        }

        // Select comparator for sort (either by name or date)
        final Comparator<Folder> comparator2;
        switch (sort.toUpperCase()) {
        case "NAME":
            comparator2 = Comparator.comparing(folder2 -> folder2.getName().toUpperCase(), comparisonOrder);
            break;
        case "DATE":
            comparator2 = Comparator.comparing(folder2 -> String.valueOf(folder2.getCreationDate()), comparisonOrder);
            break;
        default:
            throw new TDPException(DataSetErrorCodes.ILLEGAL_SORT_FOR_LIST, ExceptionContext.build().put("sort", order));
        }
        // Return sorted results
        List<DataSetMetadata> datasets = stream.filter(metadata -> !metadata.getLifecycle().importing()) //
                .map(metadata -> {
                    completeWithUserData(metadata);
                    return metadata;
                }) //
                .sorted(comparator) //

        .collect(Collectors.toList());
        if (!folderRepository.exists(folder)) {
            throw new TDPException(DataSetErrorCodes.FOLDER_NOT_FOUND, ExceptionContext.build().put("path", folder));
        }
        List<Folder> folders = StreamSupport.stream(folderRepository.children(folder).spliterator(), false).sorted(comparator2)
                .collect(Collectors.toList());

        return inventoryUtils.inventory(datasets, folders);
    }

    /**
     * Return the inventory of elements contained in a folder (which can be data sets, preparation and folders) out of
     * the given path.
     *
     * @param path the path where to look for folder elements.
     * @param name the string that must be included in the name of the folder element
     * @return the inventory of elements contained if the folder corresponding to the given path matching the given
     * name.
     */
    @RequestMapping(value = "/inventory/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List the inventory of elements contained in a folder matching the given name", produces = APPLICATION_JSON_VALUE, notes = "List the inventory of elements contained in a folder matching the given name")
    @Timed
    public Inventory inventorySearch(@RequestParam(defaultValue = "", required = false) String path,
            @RequestParam(defaultValue = "", required = true) String name) {
        return inventoryUtils.inventory(path, name);
    }

}
