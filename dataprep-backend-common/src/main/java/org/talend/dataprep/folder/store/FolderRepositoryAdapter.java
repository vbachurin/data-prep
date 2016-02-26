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

import org.apache.commons.lang.StringUtils;

public abstract class FolderRepositoryAdapter implements FolderRepository {

    protected static final String HOME_FOLDER_KEY = "HOME_FOLDER";

    protected static final char PATH_SEPARATOR = '/';

    /**
     * @param path a path as /beer/wine /foo
     * @return extract last part of a path /beer/wine -> wine /foo -> foo, / -> HOME_FOLDER
     */
    protected String extractName(String path) {
        if (StringUtils.isEmpty(path) || StringUtils.equals(path, String.valueOf(PATH_SEPARATOR))) {
            return HOME_FOLDER_KEY;
        }

        return StringUtils.contains(path, PATH_SEPARATOR) ? //
                StringUtils.substringAfterLast(path, String.valueOf(PATH_SEPARATOR)) : path;
    }

    /**
     * Remove the leading and ending '/' to the given path to use consistent paths.
     *
     * @param givenPath the path to clean.
     * @return the given path cleaned.
     */
    protected String cleanPath(String givenPath) {
        String path = givenPath;
        if (StringUtils.isEmpty(path) || StringUtils.equals(path, String.valueOf(PATH_SEPARATOR))) {
            return String.valueOf(PATH_SEPARATOR);
        }
        path = StringUtils.strip(path, String.valueOf(PATH_SEPARATOR));
        return path;
    }
}
