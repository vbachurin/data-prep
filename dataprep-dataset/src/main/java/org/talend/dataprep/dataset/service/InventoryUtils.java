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

import static java.util.stream.StreamSupport.stream;
import static org.talend.dataprep.api.folder.FolderEntry.ContentType.DATASET;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.folder.FolderEntry;
import org.talend.dataprep.dataset.store.metadata.DataSetMetadataRepository;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.FolderErrorCodes;
import org.talend.dataprep.folder.store.FolderRepository;
import org.talend.dataprep.inventory.DatasetMetadataInfo;
import org.talend.dataprep.inventory.FolderInfo;
import org.talend.dataprep.inventory.Inventory;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Inventory utils
 */
@Component
public class InventoryUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryUtils.class);

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private DataSetMetadataRepository dataSetMetadataRepository;

    /**
     * Wraps specified data set metadatas and folders and wrap them with path information (for data sets) and number of
     * data sets and preparations ( for folders).
     *
     * @param metadata the list of data set metadata of the inventory
     * @param folders the list of folders of the inventory
     * @return the inventory
     */
    public Inventory inventory(List<DataSetMetadata> metadata, List<Folder> folders) {

        if (metadata == null || folders == null) {
            throw new IllegalArgumentException("To create an inventory, you should provide non null arguments");
        }
        List<DatasetMetadataInfo> datasets = metadata.stream().map(this::wrapDataset).collect(Collectors.toList());

        List<FolderInfo> folderInfos = folders.stream().map(this::wrapFolder).collect(Collectors.toList());

        return new Inventory(datasets, folderInfos, Collections.emptyList());
    }

    /**
     * Returns the inventory of elements contained in a folder at the specified path <i>path</i> and its sub-folders at
     * any depth and matching the given name <i>name</i>.
     * 
     * @param path the specified path
     * @param name the specified name
     * @return the inventory of elements contained in a folder.
     */
    public Inventory inventory(final String path, final String name) {
        final Inventory result;

        // TODO: Add a folder to each folder repository
        final String rootPath = "/";

        if (!folderRepository.exists(path) || StringUtils.isEmpty(path)) {
            throw new TDPException(FolderErrorCodes.FOLDER_DOES_NOT_EXIST);
        }

        final String prefix = StringUtils.equals(rootPath, path) ? "" : path;


        Iterable<Folder> folderIterable = folderRepository.allFolder();
        List<Folder> folders;
        try (final Stream<Folder> stream = stream(folderIterable.spliterator(), false)) {
            folders = stream.filter(f -> StringUtils.startsWithIgnoreCase(f.getPath(), prefix)).collect(Collectors.toList());
        }

        Set<String> contentIds = new HashSet<>();
        // retrieve data sets contained in folders having path as prefix
        for (Folder folder : folders) {
            Set<String> entries;
            try (Stream<FolderEntry> stream = stream(folderRepository.entries(folder.getPath(), DATASET).spliterator(), false)) {
                entries = stream.map(FolderEntry::getContentId).collect(Collectors.toSet());
            }
            contentIds.addAll(entries);
        }

        if (StringUtils.equalsIgnoreCase(rootPath, path)) {
            // retrieve data sets contained in the root of path
            Set<String> entries = // @formatter:off
                    stream(folderRepository.entries(rootPath, DATASET).spliterator(), false)
                    .map(FolderEntry::getContentId)
                    .collect(Collectors.toSet()); // @formatter:on
            contentIds.addAll(entries);
        }

        // retrieve the data sets metadata from their ids and filter on matching name
        List<DataSetMetadata> datasets = contentIds.stream().map(dataSetMetadataRepository::get) //
                .filter(d -> d != null && d.getName() != null && StringUtils.containsIgnoreCase(d.getName(), name)) //
                .collect(Collectors.toList());

        // filter folders by name
        List<Folder> folderList = stream(folders.spliterator(), false) //
                .filter(f -> StringUtils.containsIgnoreCase(f.getName(), name)) //
                .collect(Collectors.toList());

        result = inventory(datasets, folderList);
        return result;
    }

    /**
     * Wraps a data set metadata with path information.
     *
     * @param metadata the list of data set metadata of the inventory
     * @return the data set with path information
     */
    public DatasetMetadataInfo wrapDataset(DataSetMetadata metadata) {
        Iterable<FolderEntry> entries = folderRepository.findFolderEntries(metadata.getId(), DATASET);
        FolderEntry entry = null;
        if (entries != null) {
            try {
                entry = Iterables.find(entries, Predicates.notNull());
            } catch (Exception e) {
                LOGGER.debug("all entries are null", e);
            }
        }
        return new DatasetMetadataInfo(metadata, entry != null ? entry.getFolderId() : StringUtils.EMPTY);
    }

    /**
     * Wraps folder with its number of data sets and preparations.
     * 
     * @param folder the specified folder
     * @return the folder with its number of contained data sets and its number of contained preparations
     */
    public FolderInfo wrapFolder(Folder folder) {
        int nbDatasets = (int)
                stream(folderRepository.entries(folder.getPath(), DATASET).spliterator(), false).count();
        return new FolderInfo(folder, nbDatasets, 0); // 0 because so far folder does not contain preparation
    }

}
