package org.talend.dataprep.parameters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Models a select item.
 */
public class ItemParameter {

    /**
     * the item value.
     */
    private final String value;

    /**
     * the item label.
     */
    private final String label;

    /**
     * The optional inline parameter.
     */
    @JsonProperty("parameters")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Parameter> inlineParameters;

    public ItemParameter(String value) {
        this(value, null, emptyList());
    }

    public ItemParameter(String value, List<Parameter> parameters) {
        this(value, null, parameters);
    }

    public ItemParameter(String value, String label) {
        this(value, label, emptyList());
    }

    public ItemParameter(String value, String label, List<Parameter> parameters) {
        this.value = value;
        this.label = label;
        this.inlineParameters = parameters;
    }

    public String getValue() {
        return value;
    }

    public List<Parameter> getInlineParameters() {
        return inlineParameters;
    }

    public String getLabel() {
        return label;
    }

}
