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

package org.talend.dataprep.api.folder;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.data.annotation.AccessType;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Folder implements Serializable {

    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    /**
     * repository path as /foo/bar/beer
     */
    @AccessType(AccessType.Type.PROPERTY)
    @JsonProperty("path")
    private String path;

    @JsonProperty("name")
    private String name;

    @JsonProperty("creationDate")
    private long creationDate;

    @JsonProperty("lastModificationDate")
    private long lastModificationDate;

    /**
     * Default empty constructor.
     */
    public Folder() {
        this.creationDate = System.currentTimeMillis();
        this.lastModificationDate = this.creationDate;
    }

    /**
     * Constructor with path and name.
     * 
     * @param path the folder path.
     * @param name the folder name.
     */
    public Folder(String path, String name) {
        this();
        this.path = path;
        this.name = name;
    }

    /**
     * Constructor with path and name.
     * 
     * @param path the folder path.
     * @param name the folder name.
     */
    public Folder(String path, String name, long creationDate, long lastModificationDate) {
        this.path = path;
        this.name = name;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Folder that = (Folder) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "Folder{" + "name='" + name + '\'' + ", path='" + path + '\'' + ", creationDate='" + creationDate + '\''
                + ", lastModificationDate='" + lastModificationDate + '\'' + '}';
    }

}
