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

package org.talend.dataprep.folder.store.inmemory;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.api.folder.FolderBuilder.folder;
import static org.talend.dataprep.exception.error.DataSetErrorCodes.UNABLE_TO_ADD_FOLDER;
import static org.talend.dataprep.exception.error.FolderErrorCodes.FOLDER_NOT_FOUND;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.folder.FolderContentType;
import org.talend.dataprep.api.folder.FolderEntry;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.folder.store.FolderRepository;
import org.talend.dataprep.folder.store.FolderRepositoryAdapter;
import org.talend.dataprep.folder.store.NotEmptyFolderException;
import org.talend.dataprep.security.Security;
import org.talend.dataprep.util.StringsHelper;

@Component("folderRepository#in-memory")
@ConditionalOnProperty(name = "folder.store", havingValue = "in-memory")
public class InMemoryFolderRepository extends FolderRepositoryAdapter {

    /**
     * all folders key is the path
     */
    private Map<String, Folder> foldersMap = new ConcurrentSkipListMap<>();

    /**
     * folder entries per path
     */
    private Map<String, List<FolderEntry>> folderEntriesMap = new ConcurrentSkipListMap<>();

    /**
     * Current user.
     */
    @Autowired
    private Security security;



    /**
     * @see FolderRepository#getHome()
     */
    @Override
    public Folder getHome() {
        String homeFolderId = pathToId(PATH_SEPARATOR.toString());
        if (!exists(homeFolderId)) {
            final Folder home = folder() //
                    .path(PATH_SEPARATOR.toString()) //
                    .id(homeFolderId) //
                    .name(HOME_FOLDER_KEY) //
                    .ownerId(security.getUserId()) //
                    .parentId(null) //
                    .build();
            foldersMap.put(home.getId(), home);
        }
        return foldersMap.get(homeFolderId);
    }


    /**
     * @see FolderRepository#exists(String)
     */
    @Override
    public boolean exists(String folderId) {
        return foldersMap.containsKey(folderId);
    }

    /**
     * @see FolderRepository#addFolder(String, String)
     */
    @Override
    public Folder addFolder(String parentFolderId, String givenPath) {

        // check the parent
        Folder parent = getFolderById(parentFolderId);
        if (parent == null) {
            throw new TDPException(UNABLE_TO_ADD_FOLDER, build().put("path", parentFolderId + '/' + givenPath));
        }

        // new path must not be empty nor /
        String fullPathToCreate = getFullPathToCreate(parent, givenPath);
        if (StringUtils.isBlank(fullPathToCreate) || PATH_SEPARATOR.toString().equals(parentFolderId)) {
            throw new TDPException(UNABLE_TO_ADD_FOLDER, build().put("path", parentFolderId + '/' + givenPath));
        }

        // get parent folder
        List<Folder> tree = buildFlatFolderTree(fullPathToCreate);

        // create all the folders
        for (Folder folderToAdd : tree) {

            final String currentFolderId = pathToId(folderToAdd.getPath());
            if (exists(currentFolderId)) {
                parent = getFolderById(currentFolderId);
            }
            else {
                Folder nextChild = folder() //
                        .id(currentFolderId) //
                        .path(folderToAdd.getPath()) //
                        .name(extractName(folderToAdd.getPath())) //
                        .ownerId(security.getUserId()) //
                        .parentId(parent.getId()) //
                        .build();
                foldersMap.put(currentFolderId, nextChild);
                parent = nextChild;
            }

        }

        return parent;
    }

    @Override
    public Iterable<Folder> children(String folderId) {
        return this.foldersMap.values().stream().filter(f -> StringUtils.equals(folderId, f.getParentId())).collect(Collectors.toList());
    }

    @Override
    public Folder getFolderById(String folderId) {
        if (foldersMap.containsKey(folderId)) {
            return foldersMap.get(folderId);
        }
        else {
            throw new TDPException(FOLDER_NOT_FOUND, null, build().put("path", folderId));
        }
    }

    @Override
    public Iterable<Folder> searchFolders(String queryString, boolean strict) {
        final List<Folder> children = new ArrayList<>();
        this.foldersMap.values().stream().forEach(folder -> {
            String cleanPath = cleanPath(folder.getPath());

            if (StringUtils.contains(cleanPath, PATH_SEPARATOR)) {
                cleanPath = StringUtils.substringAfterLast(cleanPath, PATH_SEPARATOR.toString());
            }

            if (StringsHelper.match(cleanPath, queryString, strict)) {
                children.add(folder);
            }
        });

        return children;
    }

    @Override
    public Folder renameFolder(String folderId, String newName) {

        final Folder folder = getFolderById(folderId);

        final String newPath = StringUtils.replaceOnce(folder.getPath(), cleanPath(folder.getName()), cleanPath(newName));
        folder.setPath(newPath);
        folder.setName(extractName(folder.getPath()));
        folder.setLastModificationDate(new Date().getTime());
        folder.setId(pathToId(folder.getPath()));
        foldersMap.put(folder.getId(), folder);
        foldersMap.remove(folderId);

        updateParentForEntries(folderId, folder.getId());

        // first update children
        children(folderId).forEach(child -> updatePathFromParent(folder, child));

        return folder;
    }

    private void updateParentForEntries(String oldId, String newId) {

        if (!folderEntriesMap.containsKey(oldId)) {
            return;
        }

        final List<FolderEntry> entries = folderEntriesMap.get(oldId);
        entries.stream().forEach(e -> e.setFolderId(newId));

        folderEntriesMap.remove(oldId);
        folderEntriesMap.put(newId, entries);
    }


    /**
     * Update the path from its parent.
     * @param folder the folder to update
     */
    private void updatePathFromParent(final Folder parent, Folder folder) {

        String oldId = folder.getId();

        String newPath = parent.getPath() + PATH_SEPARATOR + folder.getName();
        folder.setPath(newPath);
        folder.setLastModificationDate(new Date().getTime());
        folder.setId(pathToId(folder.getPath()));
        folder.setParentId(parent.getId());
        foldersMap.put(folder.getId(), folder);
        foldersMap.remove(oldId);

        updateParentForEntries(oldId, folder.getId());

        // recursive call to apply the change to all the tree
        children(folder.getId()).forEach(child -> updatePathFromParent(parent, child));
    }


    @Override
    public void clear() {
        this.foldersMap.clear();
        this.folderEntriesMap.clear();
    }

    @Override
    public FolderEntry addFolderEntry(FolderEntry folderEntry, String folderId) {
        folderEntry.setFolderId(folderId);
        List<FolderEntry> folderEntries = folderEntriesMap.get(folderEntry.getFolderId());
        if (folderEntries == null) {
            folderEntries = new ArrayList<>();
            folderEntriesMap.put(folderEntry.getFolderId(), folderEntries);
        }
        folderEntries.add(folderEntry);
        return folderEntry;
    }

    @Override
    public void copyFolderEntry(FolderEntry folderEntry, String toId) {
        FolderEntry cloned = new FolderEntry(folderEntry.getContentType(), folderEntry.getContentId());
        cloned.setFolderId(toId);
        this.addFolderEntry(cloned, cloned.getFolderId());
    }

    @Override
    public void moveFolderEntry(FolderEntry folderEntry, String fromId, String toId) {
        List<FolderEntry> entries = folderEntriesMap.get(folderEntry.getFolderId());
        if (entries != null) {
            entries.remove(folderEntry);
        }
        folderEntry.setFolderId(toId);
        addFolderEntry(folderEntry, toId);
    }

    @Override
    public Iterable<FolderEntry> entries(String folderId, FolderContentType contentType) {
        List<FolderEntry> folderEntries = folderEntriesMap.get(folderId);
        if (folderEntries == null) {
            folderEntries = new ArrayList<>();
        }

        return folderEntries.stream().filter(e -> e.getContentType() == contentType).collect(Collectors.toList());
    }

    @Override
    public Iterable<FolderEntry> findFolderEntries(String contentId, FolderContentType contentType) {
        List<FolderEntry> entries = new ArrayList<>();

        this.folderEntriesMap.values().stream().forEach(folderEntries -> folderEntries.stream().forEach(folderEntry -> {
            if (equalsIgnoreCase(contentId, folderEntry.getContentId()) && contentType.equals(folderEntry.getContentType())) {
                entries.add(folderEntry);
            }
        }));

        return entries;
    }

    @Override
    public void removeFolder(String folderId) throws NotEmptyFolderException {

        final Folder folder = getFolderById(folderId);

        // make sure the whole folder tree is empty
        if (!isFolderTreeEmpty(folder)) {
            throw new NotEmptyFolderException(folderId + " is not empty");
        }

        // delete the whole folder tree
        deleteTree(folder);

    }

    private void deleteTree(Folder rootFolder) {
        foldersMap.remove(rootFolder.getId());
        children(rootFolder.getId()).forEach(this::deleteTree);
    }

    /**
     * @param rootFolder where the folder tree starts.
     * @return true if this folder and all its children are empty.
     */
    private boolean isFolderTreeEmpty(Folder rootFolder) {

        // check the current rootFolder
        final List<FolderEntry> entries = folderEntriesMap.get(rootFolder.getId());
        if (entries != null && !entries.isEmpty()) {
            return false;
        }

        // and all its children
        for (Folder next : children(rootFolder.getId())) {
            if (!isFolderTreeEmpty(next)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void removeFolderEntry(String folderId, String contentId, FolderContentType contentType) {
        List<FolderEntry> entries = folderEntriesMap.get(folderId);
        final FolderEntry entry = new FolderEntry(contentType, contentId);
        entry.setFolderId(folderId);
        entries.remove(entry);
    }

    @Override
    public long size() {
        return this.foldersMap.size() - 1L; // remove root folder
    }


    /**
     * @see FolderRepository#locateEntry(String, FolderContentType)
     */
    @Override
    public Folder locateEntry(String contentId, FolderContentType type) {
        return folderEntriesMap.entrySet().stream() //
                .filter(e -> e.getValue().stream().filter(filterFolderEntry(contentId, type)).findFirst().isPresent()) //
                .map(e -> foldersMap.get(e.getKey())) //
                // return the first (should be the only one) or null if not found.
                .findFirst().orElse(null);
    }


    /**
     * Return a filter for content id and folder entry type.
     *
     * @param contentId the wanted content id.
     * @param type      the wanted folder entry type.
     * @return true if the given entry matches the content id and content type.
     */
    private Predicate<FolderEntry> filterFolderEntry(String contentId, FolderContentType type) {
        return entry -> entry.getContentId().equals(contentId) && entry.getContentType() == type;
    }


}
