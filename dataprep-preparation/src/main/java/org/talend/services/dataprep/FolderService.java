package org.talend.services.dataprep;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.folder.FolderEntry;
import org.talend.dataprep.api.folder.FolderInfo;
import org.talend.dataprep.api.folder.FolderTreeNode;
import org.talend.dataprep.metrics.Timed;

/**
 * This service provides all operations needed to organize / create folders for the Data Prep preparations.
 */
@Service(name = "FolderService")
public interface FolderService {

    /**
     * List all child folders of the one as parameter.
     *
     * @param sort Sort key (by name or date, default to date if not specified).
     * @param id the current folder where to look for children.
     * @param order Order for sort key ("desc" or "asc", defaults to "desc" if not specified).
     * @return direct sub folders for the given id.
     */
    @RequestMapping(value = "/folders/{id}/children", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<Folder> children(@PathVariable String id, //
            @RequestParam(defaultValue = "MODIF") String sort, //
            @RequestParam(defaultValue = "DESC") String order);

    /**
     * Get a folder metadata with its hierarchy
     *
     * @param id the folder id.
     * @return the folder metadata with its hierarchy.
     */
    @RequestMapping(value = "/folders/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    FolderInfo getFolderAndHierarchyById(@PathVariable(value = "id") String id);

    /**
     * Search for folders.
     *
     * @param name the folder name to search.
     * @param strict strict mode means the name is the full name.
     * @return the folders whose part of their name match the given path.
     */
    @RequestMapping(value = "/folders/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<Folder> search(@RequestParam String name, @RequestParam(required = false) boolean strict);

    /**
     * Add a folder.
     *
     * @param parentId where to add the folder.
     * @return the created folder.
     */
    @RequestMapping(value = "/folders", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    Folder addFolder(@RequestParam String parentId, @RequestParam String path);

    /**
     * Remove the folder. Throws an exception if the folder, or one of its sub folders, contains an entry.
     *
     * @param id the id that points to the folder to remove.
     */
    @RequestMapping(value = "/folders/{id}", method = DELETE)
    @Timed
    void removeFolder(@PathVariable String id);

    /**
     * Rename the folder to the new id.
     *
     * @param id where to look for the folder.
     * @param newName the new folder id.
     */
    @RequestMapping(value = "/folders/{id}/name", method = PUT)
    @Timed
    void renameFolder(@PathVariable String id, @RequestBody String newName);

    /**
     * Remove a folder entry.
     * TODO This is not used : should remove ?
     *
     * @param folderId where to look for the entry.
     * @param contentId the content id.
     * @param contentType the entry content type.
     */
    @Deprecated
    @RequestMapping(value = "/folders/entries/{contentType}/{id}", method = DELETE)
    @Timed
    void deleteFolderEntry(@PathVariable(value = "contentType") String contentType, //
            @PathVariable(value = "id") String contentId, //
            @RequestParam String folderId);

    /**
     * Return the list of folder entries out of the given path.
     *
     * @param path the path where to look for entries.
     * @param contentType the type of wanted entries.
     * @return the list of folder entries out of the given path.
     */
    @RequestMapping(value = "/folders/entries", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @Deprecated
    Iterable<FolderEntry> entries(@RequestParam String path, @RequestParam String contentType);

    /**
     * List all folders.
     * 
     * @return A list of all folders.
     */
    @RequestMapping(value = "/folders/tree", method = GET)
    @Timed
    FolderTreeNode getTree();
}
