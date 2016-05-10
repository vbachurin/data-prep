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

package org.talend.dataprep.folder.store;

import static org.talend.dataprep.api.folder.FolderBuilder.folder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.security.Security;

/**
 * Abstract class used to share code between FolderRepository implementations.
 */
public abstract class FolderRepositoryAdapter implements FolderRepository {

    /** Constant for HOME_FOLDER. */
    protected static final String HOME_FOLDER_KEY = "HOME";

    /** Constant for path separator. */
    protected static final Character PATH_SEPARATOR = '/';
    /** Constant for the content type string. */
    protected static final String CONTENT_TYPE="contentType";
    /** Constant for the content id string. */
    protected static final String CONTENT_ID="contentId";
    /** Constant to prevent string literal duplication. */
    protected static final String FOLDER_ID = "folderId";
    /** Constant to prevent string literal duplication. */
    protected static final String OWNER_ID = "ownerId";
    /** Constant to prevent string literal duplication. */
    protected static final String PATH = "path";

    /** Current user. */
    @Autowired
    protected Security security;

    /**
     * @param path a path as /beer/wine /foo
     * @return extract last part of a path /beer/wine -> wine /foo -> foo, / -> HOME_FOLDER
     */
    protected String extractName(String path) {
        if (StringUtils.isEmpty(path) || StringUtils.equals(path, PATH_SEPARATOR.toString())) {
            return HOME_FOLDER_KEY;
        }

        return StringUtils.contains(path, PATH_SEPARATOR) ? //
                StringUtils.substringAfterLast(path, PATH_SEPARATOR.toString()) : path;
    }

    /**
     * Remove the trailing '/' to the given path to use consistent paths.
     *
     * @param givenPath the path to clean.
     * @return the given path cleaned.
     */
    protected String cleanPath(String givenPath) {
        String path = givenPath;
        if (StringUtils.isEmpty(path) || StringUtils.equals(path, PATH_SEPARATOR.toString())) {
            return PATH_SEPARATOR.toString();
        }
        path = StringUtils.stripEnd(path, PATH_SEPARATOR.toString());
        if (!path.startsWith(PATH_SEPARATOR.toString())) {
            path = PATH_SEPARATOR + path;
        }
        return path;
    }

    /**
     * Return the folder tree as a flat list out of the given givenPath.
     *
     * For instance : /marketing/2016/q1 returns [/, /marketing, /marketing/2016, /marketing/2016/q1]
     *
     * @param givenPath the full givenPath.
     * @return the folder tree as a flat list out of the given givenPath.
     */
    protected List<Folder> buildFlatFolderTree(String givenPath) {

        List<Folder> result = new ArrayList<>();

        String path = cleanPath(givenPath);
        String[] folders = path.split(PATH_SEPARATOR.toString());

        if (folders.length == 0) {
            folders = new String[]{"/"};
        }

        for (int i=0; i<folders.length; i++) {

            // build the givenPath
            String currentPath = "";
            for (int j=0; j<=i; j++) {
                currentPath += folders[j] + PATH_SEPARATOR;
            }
            currentPath = cleanPath(currentPath);
            result.add(folder() //
                    .path(currentPath) //
                    .name(extractName(currentPath)) //
                    .ownerId(security.getUserId()) //
                    .build());
        }

        return result;
    }

    /**
     * Return the parent path of the given path, e.g. /2016/q1 => /2016.
     *
     * @param givenPath the path to get the parent from.
     * @return the parent path.
     */
    protected String getParentPath(String givenPath) {
        String path = cleanPath(givenPath);
        if (StringUtils.equals(PATH_SEPARATOR.toString(), path)) {
            return null;
        }
        path = path.substring(0, path.lastIndexOf(PATH_SEPARATOR));
        path = cleanPath(path);
        return path;
    }

    /**
     * @param id the id to convert to path.
     * @return the id converted to path
     */
    protected String idToPath(String id) {
        try {
            return new String(Base64.getDecoder().decode(id));
        }
        catch (final NullPointerException | IllegalArgumentException e) { //NOSONAR no need to log such an exception
            return id;
        }
    }

    /**
     * @param path the path to convert to id.
     * @return the path converted to id.
     */
    protected String pathToId(String path) {
        if (path == null) {
            return null;
        }
        return new String(Base64.getEncoder().encode(path.getBytes()));
    }

    /**
     * Return the full path to create, e.g. '/home/2016' + '/q1/january' => '/home/2016/q1/january'.
     *
     * @param parent the parent folder.
     * @param path the path to create from the parent.
     * @return the full path to create.
     */
    protected String getFullPathToCreate(Folder parent, String path) {
        String fullPathToCreate = parent.getPath() + cleanPath(path);
       
        // special case when dealing with "/" as parent path ('/' + '/toto' --> '//toto', and it should be '/toto' only)
        if (fullPathToCreate.startsWith("//")) {
            fullPathToCreate = fullPathToCreate.substring(1);
        }
        return fullPathToCreate;
    }
}
