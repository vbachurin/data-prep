package org.talend.dataprep.transformation.api.action.parameters;

import java.io.Serializable;

import org.talend.dataprep.i18n.MessagesBundle;

public class Parameter implements Serializable {

    private final String name;

    private final String type;

    private final String defaultValue;

    private final boolean implicit;

    public Parameter(String name, String type) {
        this(name, type, null, false);
    }

    public Parameter(String name, String type, String defaultValue) {
        this(name, type, defaultValue, false);
    }

    public Parameter(final String name, final String type, final String defaultValue, final boolean implicit) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.implicit = implicit;
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
}
