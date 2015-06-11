package org.talend.dataprep.transformation.api.action.parameters;

import org.talend.dataprep.i18n.MessagesBundle;

public class Item {

    private final String name;

    private final String category;

    private final Value[] values;

    public Item(String name, String category, Value[] values) {
        super();
        this.name = name;
        this.category = category;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    /**
     * @return The label of the parameter, translated in the user locale.
     * @see MessagesBundle
     */
    public String getLabel() {
        return MessagesBundle.getString("parameter." + getName() + ".label"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @return The description of the parameter, translated in the user locale.
     * @see MessagesBundle
     */
    public String getDescription() {
        return MessagesBundle.getString("parameter." + getName() + ".desc"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String getCategory() {
        return category;
    }

    public Value[] getValues() {
        return values;
    }

    public static class Value {

        final String name;

        final Parameter[] parameters;

        boolean isDefault = false;

        public Value(String name, Parameter... parameters) {
            super();
            this.name = name;
            this.parameters = parameters;
        }

        public Value(String name, boolean isDefault, Parameter... parameters) {
            super();
            this.name = name;
            this.isDefault = isDefault;
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public boolean isDefault() {
            return this.isDefault;
        }

        public void setDefault(boolean defaultValue) {
            this.isDefault = defaultValue;
        }

        public Parameter[] getParameters() {
            return parameters;
        }

    }
}
