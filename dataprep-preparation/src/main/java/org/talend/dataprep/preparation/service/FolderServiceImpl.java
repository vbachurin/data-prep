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

package org.talend.dataprep.preparation.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.api.folder.FolderContentType.PREPARATION;
import static org.talend.dataprep.exception.error.FolderErrorCodes.FOLDER_NOT_EMPTY;
import static org.talend.dataprep.exception.error.FolderErrorCodes.FOLDER_NOT_FOUND;
import static org.talend.dataprep.util.SortAndOrderHelper.getFolderComparator;

import java.util.List;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.folder.*;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.FolderErrorCodes;
import org.talend.dataprep.folder.store.FolderRepository;
import org.talend.dataprep.folder.store.NotEmptyFolderException;
import org.talend.dataprep.security.Security;
import org.talend.services.dataprep.FolderService;

@ServiceImplementation
public class FolderServiceImpl implements FolderService {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderServiceImpl.class);

    /** Where the folders are stored. */
    @Autowired
    private FolderRepository folderRepository;

    /** DataPrep abstraction to the underlying security (whether it's enabled or not). */
    @Autowired
    private Security security;

    /**
     * List direct sub folders for the given id.
     *
     * @param id the current folder where to look for children.
     * @return direct sub folders for the given id.
     */
    @Override
    public Iterable<Folder> children(String id, String sort, String order) {
        if (!folderRepository.exists(id)) {
            throw new TDPException(FOLDER_NOT_FOUND, build().put("id", id));
        }

        Iterable<Folder> children = folderRepository.children(id);

        // update the number of preparations in each children
        children.forEach(f -> {
            final long count = stream(folderRepository.entries(f.getId(), PREPARATION).spliterator(), false).count();
            f.setNbPreparations(count);
        });

        // sort the folders
        children = StreamSupport.stream(children.spliterator(), false) //
                .sorted(getFolderComparator(sort, order)) //
                .collect(toList());

        LOGGER.info("found {} children for {}", stream(children.spliterator(), false).count(), id);
        return children;
    }

    /**
     * Get a folder metadata with its hierarchy
     *
     * @param id the folder id.
     * @return the folder metadata with its hierarchy.
     */
    @Override
    public FolderInfo getFolderAndHierarchyById(final String id) {
        final Folder folder = folderRepository.getFolderById(id);
        final List<Folder> hierarchy = folderRepository.getHierarchy(folder);

        return new FolderInfo(folder, hierarchy);
    }

    /**
     * Search for folders.
     *
     * @param name the folder name to search.
     * @param strict strict mode means the name is the full name.
     * @return the folders whose part of their name match the given path.
     */
    @Override
    public Iterable<Folder> search(final String name, final boolean strict) {
        final Iterable<Folder> folders = folderRepository.searchFolders(name, strict);

        int foldersFound = 0;
        for (Folder folder : folders) {
            final long count = stream(folderRepository.entries(folder.getId(), PREPARATION).spliterator(), false).count();
            folder.setNbPreparations(count);
            foldersFound++;
        }

        LOGGER.info("Found {} folder(s) searching for {}", foldersFound, name);

        return folders;
    }

    /**
     * Add a folder.
     *
     * @param parentId where to add the folder.
     * @return the created folder.
     */
    @Override
    public Folder addFolder(String parentId, String path) {
        return folderRepository.addFolder(parentId, path);
    }

    /**
     * Remove the folder. Throws an exception if the folder, or one of its sub folders, contains an entry.
     *
     * @param id the id that points to the folder to remove.
     */
    @Override
    public void removeFolder(String id) {
        try {
            folderRepository.removeFolder(id);
        } catch (NotEmptyFolderException e) {
            throw new TDPException(FOLDER_NOT_EMPTY, e);
        }
    }

    /**
     * Rename the folder to the new id.
     *
     * @param id where to look for the folder.
     * @param newName the new folder id.
     */
    @Override
    public void renameFolder(String id, String newName) {
        folderRepository.renameFolder(id, newName);
    }

    /**
     * Remove a folder entry.
     * TODO This is not used : should remove ?
     *
     * @param folderId where to look for the entry.
     * @param contentId the content id.
     * @param contentType the entry content type.
     */
    @Override
    public void deleteFolderEntry(final String contentType, //
            final String contentId, //
            final String folderId) {
        try {
            FolderContentType checkedContentType = FolderContentType.fromName(contentType);
            folderRepository.removeFolderEntry(folderId, contentId, checkedContentType);
        } catch (IllegalArgumentException exc) {
            throw new TDPException(FolderErrorCodes.UNABLE_TO_DELETE_FOLDER_ENTRY, exc);
        }
    }

    /**
     * Return the list of folder entries out of the given path.
     *
     * @param path the path where to look for entries.
     * @param contentType the type of wanted entries.
     * @return the list of folder entries out of the given path.
     */
    @Override
    public Iterable<FolderEntry> entries(String path, String contentType) {
        try {
            FolderContentType checkedContentType = FolderContentType.fromName(contentType);
            return folderRepository.entries(path, checkedContentType);
        } catch (IllegalArgumentException exc) {
            throw new TDPException(FolderErrorCodes.UNABLE_TO_LIST_FOLDER_ENTRIES, exc);
        }
    }

    @Override
    public FolderTreeNode getTree() {
        final Folder home = folderRepository.getHome();
        return getTree(home);
    }

    private FolderTreeNode getTree(final Folder root) {
        final Iterable<Folder> children = folderRepository.children(root.getId());
        final List<FolderTreeNode> childrenSubtrees = StreamSupport.stream(children.spliterator(), false).map(this::getTree)
                .collect(toList());
        return new FolderTreeNode(root, childrenSubtrees);
    }
}
