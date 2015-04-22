package org.talend.dataprep.transformation.api.action.metadata;

import org.talend.dataprep.i18n.MessagesBundle;

public class Parameter {

    private final String name;

    private final String type;

    private final String defaultValue;

    public Parameter(String name, String type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
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

    public String getType() {
        return type;
    }

    public String getDefault() {
        return defaultValue;
    }
}
