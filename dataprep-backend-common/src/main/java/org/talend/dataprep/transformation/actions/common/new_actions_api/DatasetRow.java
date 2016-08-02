package org.talend.dataprep.transformation.actions.common.new_actions_api;

import java.util.Map;
import java.util.TreeMap;

/**
 * Simplest possible access to a dataset row. Values are bound to their column ID and may be changed. The row may also be marked
 * as deleted. It may be also a good idea to give access to columns metadata from here and have utilisty values access using
 * column name for instance.
 */
public class DatasetRow {

    private Map<String, String> values = new TreeMap<>();

    private boolean deleted = false;

    public String getValue(String columnId) {
        return values.get(columnId);
    }

    public void setValue(String columnId, String value) {
        values.put(columnId, value);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
