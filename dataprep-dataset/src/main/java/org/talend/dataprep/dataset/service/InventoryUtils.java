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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
     * @param metadatas
     * @param folders
     * @return
     */
    public Inventory inventory(List<DataSetMetadata> metadatas, List<Folder> folders) {

        if (metadatas == null || folders == null) {
            throw new IllegalArgumentException("To create an inventory, you should provide non null arguments");
        }
        List<DatasetMetadataInfo> datasets = metadatas.stream().map(d -> wrapDataset(d)).collect(Collectors.toList());

        List<FolderInfo> folderInfos = folders.stream().map(f -> wrapFolder(f)).collect(Collectors.toList());

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
    public Inventory inventory(String path, String name) {
        Inventory result = null;

        if (!folderRepository.exists(path)){
            throw  new TDPException(FolderErrorCodes.FOLDER_DOES_NOT_EXIST);
        }

        Iterable<Folder> folderIterable = folderRepository.allFolder();
        List<Folder> folders = StreamSupport.stream(folderIterable.spliterator(), false).filter(f -> f.getPath().startsWith(path))
                .collect(Collectors.toList());

        Set<String> contentIds = new HashSet<>();
        // retrieve data sets contained in folders having path as prefix
        for (Folder folder : folders) {
            Set<String> entries = StreamSupport
                    .stream(folderRepository.entries(folder.getPath(), FolderEntry.ContentType.DATASET).spliterator(), false)
                    .map(f -> f.getContentId()).collect(Collectors.toSet());
            contentIds.addAll(entries);
        }

        // retrieve data sets contained the root of path
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

        result = inventory(datasets, folderList);
        return result;
    }

    /**
     * Wraps a data set with path information.
     * 
     * @param dataSetMetadata
     * @return
     */
    public DatasetMetadataInfo wrapDataset(DataSetMetadata dataSetMetadata) {
        Iterable<FolderEntry> entries = folderRepository.findFolderEntries(dataSetMetadata.getId(),
                FolderEntry.ContentType.DATASET);
        FolderEntry entry = null;
        if (entries != null) {
            try {
                entry = Iterables.find(entries, Predicates.notNull());
            }catch(Exception e){
                LOGGER.debug("all entries are null");
            }
        }
        return new DatasetMetadataInfo(dataSetMetadata, entry != null ? entry.getFolderId() : StringUtils.EMPTY);
    }

    /**
     * Wraps folder with its number of datasets and preparations.
     * 
     * @param folder
     * @return
     */
    public FolderInfo wrapFolder(Folder folder) {
        int nbDatasets = (int) StreamSupport
                .stream(folderRepository.entries(folder.getPath(), FolderEntry.ContentType.DATASET).spliterator(), false).count();
        return new FolderInfo(folder, nbDatasets, 0); // 0 because so far folder does not contain preparation
    }

}
