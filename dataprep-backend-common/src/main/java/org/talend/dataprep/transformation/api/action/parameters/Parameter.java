package org.talend.dataprep.transformation.api.action.parameters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.i18n.MessagesBundle;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Bean that models action parameter.
 */
public class Parameter implements Serializable {

    /** The parameter name. */
    private final String name;
    /** The parameter type. */
    private final String type;
    /** The parameter default value. */
    private final String defaultValue;
    /** True if the parameter is not displayed to the user. */
    private final boolean implicit;
    /** True if the parameter can be blank. */
    private final boolean canBeBlank;

    /** The configuration. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> configuration;

    /**
     * Minimal default constructor.
     *
     * @param name The parameter name.
     * @param type The parameter type.
     */
    public Parameter(String name, String type) {
        this(name, type, null, false);
    }

    /**
     * Constructor with a default value.
     *
     * @param name The parameter name.
     * @param type The parameter type.
     * @param defaultValue the parameter default value.
     */
    public Parameter(String name, String type, String defaultValue) {
        this(name, type, defaultValue, false);
    }

    /**
     * Constructor with a default value and the implicit flag.
     *
     * @param name The parameter name.
     * @param type The parameter type.
     * @param defaultValue the parameter default value.
     * @param implicit true if the parameter is implicit.
     */
    public Parameter(final String name, final String type, final String defaultValue, final boolean implicit) {
        this(name, type, defaultValue, implicit, true);
    }

    /**
     * Full constructor.
     *
     * @param name The parameter name.
     * @param type The parameter type.
     * @param defaultValue the parameter default value.
     * @param implicit true if the parameter is implicit.
     * @param canBeBlank True if the parameter can be blank.
     */
    public Parameter(final String name, final String type, final String defaultValue, final boolean implicit,
            final boolean canBeBlank) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.implicit = implicit;
        this.canBeBlank = canBeBlank;
        this.configuration = new HashMap<>();
    }

    protected void addConfiguration(String name, Object configuration) {
        this.configuration.put(name, configuration);
    }

    /**
     * the unique identifier of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * the label of the parameter, translated in the user locale.
     */
    public String getLabel() {
        return MessagesBundle.getString("parameter." + getName() + ".label"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * the description of the parameter, translated in the user locale.
     */
    public String getDescription() {
        return MessagesBundle.getString("parameter." + getName() + ".desc"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * the type of the parameter
     */
    public String getType() {
        return type;
    }

    /**
     * the parameter's default value
     */
    public String getDefault() {
        return defaultValue;
    }

    /**
     * indicates if the parameter is implicit (not to ask to user directly)
     */
    public boolean isImplicit() {
        return implicit;
    }

    /**
     * indicates if the parameter value can be blank
     */
    public boolean isCanBeBlank() {
        return canBeBlank;
    }

    /**
     * @return the parameter configuration
     */
    public Map<String, Object> getConfiguration() {
        return configuration;
    }
}
