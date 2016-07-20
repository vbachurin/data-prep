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

package org.talend.dataprep.parameters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parameter that should be displayed as a select box in the UI.
 */
public class SelectParameter extends Parameter implements Serializable {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = 1L;

    private final boolean isRadio;

    /**
     * The select items.
     */
    @JsonIgnore // will be part of the Parameter#configuration
    private List<ItemParameter> items;

    /** True if multiple items can be selected. */
    @JsonIgnore // will be part of the Parameter#configuration
    private boolean multiple;

    /**
     * Private constructor to ensure the use of builder.
     *
     * @param name         The parameter name.
     * @param defaultValue The parameter default value.
     * @param implicit     True if the parameter is implicit.
     * @param canBeBlank   True if the parameter can be blank.
     * @param items        List of items for this select parameter.
     * @param multiple     True if multiple selection is allowed.
     * @param isRadio      <code>true</code> if the rendering code should prefer radio buttons instead of drop down list.
     */
    private SelectParameter(String name, String defaultValue, boolean implicit, boolean canBeBlank, List<ItemParameter> items,
            boolean multiple, boolean isRadio) {
        super(name, ParameterType.SELECT, defaultValue, implicit, canBeBlank);
        this.isRadio = isRadio;
        setItems(items);
        setMultiple(multiple);
    }

    /**
     * @return <code>true</code> if the rendering code should prefer radio buttons instead of drop down list.
     */
    public boolean isRadio() {
        return isRadio;
    }

    public List<ItemParameter> getItems() {
        return items;
    }

    public void setItems(List<ItemParameter> items) {
        addConfiguration("values", items);
        this.items = items;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        addConfiguration("multiple", multiple);
        this.multiple = multiple;
    }

    /**
     * Builder used to simplify the syntax of creation.
     */
    public static class Builder {

        /** The parameter name. */
        private String name = "";

        /** List of items. */
        private List<ItemParameter> items = new ArrayList<>();

        /** The parameter default value. */
        private String defaultValue = "";

        /** True if the parameter is not displayed to the user. */
        private boolean implicit = false;

        /**
         * True if the parameter can be blank.
         */
        private boolean canBeBlank = false;

        /**
         * True if the selection is multiple.
         */
        private boolean multiple = false;

        /**
         * True if rendering should prefer radio buttons to render parameters choices
         */
        private boolean isRadio = false;

        /**
         * @return A SelectParameter builder.
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Set the name of the select parameter.
         *
         * @param name the name of the select parameter.
         * @return the builder to carry on building the column.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the defaultValue of the select parameter.
         *
         * @param defaultValue the default value of the select parameter.
         * @return the builder to carry on building the column.
         */
        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Set the implicit of the select parameter.
         *
         * @param implicit true if the parameter is implicit.
         * @return the builder to carry on building the column.
         */
        public Builder implicit(boolean implicit) {
            this.implicit = implicit;
            return this;
        }

        /**
         * Set the canBeBlank of the select parameter.
         *
         * @param canBeBlank true if the parameter is implicit.
         * @return the builder to carry on building the column.
         */
        public Builder canBeBlank(boolean canBeBlank) {
            this.canBeBlank = canBeBlank;
            return this;
        }

        /**
         * Add an item to the select parameter builder.
         *
         * @param value     the item value.
         * @param parameter the item optional parameter.
         * @return the builder to carry on building the column.
         * @deprecated Always use {@link #item(String, String, Parameter...)} that supplies an i18n label.
         */
        @Deprecated
        public Builder item(String value, Parameter... parameter) {
            this.items.add(new ItemParameter(value, value, Arrays.asList(parameter)));
            return this;
        }

        /**
         * Add an item to the select parameter builder.
         *
         * @param value the item value.
         * @param label the item label
         * @return the builder to carry on building the column.
         */
        public Builder item(String value, String label, Parameter... parameter) {
            final ItemParameter item = new ItemParameter(value, label, Arrays.asList(parameter));
            this.items.add(item);
            return this;
        }

        /**
         * Add all items to the select parameter builder.
         *
         * @param items the item name.
         * @return the builder to carry on building the column.
         */
        public Builder items(List<ItemParameter> items) {
            this.items.addAll(items);
            return this;
        }

        public Builder radio(boolean isRadio) {
            this.isRadio = isRadio;
            return this;
        }

        /**
         * Build the column with the previously entered values.
         *
         * @return the built column metadata.
         */
        public SelectParameter build() {
            return new SelectParameter(name, defaultValue, implicit, canBeBlank, items, multiple, isRadio);
        }
    }

}
