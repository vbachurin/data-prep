package org.talend.dataprep.transformation.api.action.metadata;

public class Parameter {

    String name;

    String type;

    String defaultValue;

    public Parameter(String name, String type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDefault() {
        return defaultValue;
    }
}
