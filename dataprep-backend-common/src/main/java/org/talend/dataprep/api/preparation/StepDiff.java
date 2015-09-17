package org.talend.dataprep.api.preparation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StepDiff implements Serializable {
    private List<String> createdColumns = new ArrayList<>(0);

    public List<String> getCreatedColumns() {
        return createdColumns;
    }

    public void setCreatedColumns(List<String> createdColumns) {
        this.createdColumns = createdColumns;
    }
}
