package org.talend.dataprep.transformation.api.action.parameters;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Parameter that should be displayed as a select box in the UI.
 */
public class SelectParameter extends Parameter {

    /**
     * Private constructor to ensure the use of builder.
     *
     * @param name The parameter name.
     * @param defaultValue The parameter default value.
     * @param implicit True if the parameter is implicit.
     * @param canBeBlank True if the parameter can be blank.
     * @param items List of items for this select parameter.
     * @param multiple True if multiple selection is allowed.
     */
    private SelectParameter(String name, String defaultValue, boolean implicit, boolean canBeBlank, List<Item> items,
            boolean multiple) {
        super(name, ParameterType.SELECT.asString(), defaultValue, implicit, canBeBlank);
        addConfiguration("values", items);
        addConfiguration("multiple", multiple);
    }

    /**
     * Models a select item.
     */
    private static class Item {

        /** The item name. */
        private String name;

        /** the item value. */
        private String value;

        /** The optional inline parameter. */
        @JsonProperty("parameter")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Parameter inlineParameter;

        /**
         * Create a select Item.
         *
         * @param name the item name.
         * @param value the item value.
         * @param parameter the item optional parameter.
         */
        public Item(String name, String value, Parameter parameter) {
            this.name = name;
            this.value = value;
            this.inlineParameter = parameter;
        }

        /**
         * @return the Name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the Value
         */
        public String getValue() {
            return value;
        }

        /**
         * @return the InlineParameter
         */
        public Parameter getInlineParameter() {
            return inlineParameter;
        }
    }

    /**
     * Builder used to simplify the syntax of creation.
     */
    public static class Builder {

        /** The column name. */
        private String name = "";

        /** List of items. */
        private List<Item> items = new ArrayList<>();

        /** The parameter default value. */
        private String defaultValue = "";

        /** True if the parameter is not displayed to the user. */
        private boolean implicit = false;

        /** True if the parameter can be blank. */
        private boolean canBeBlank = false;

        /** True if the selection is multiple. */
        private boolean multiple = false;

        /**
         * @return A SelectParameter builder.
         */
        public static SelectParameter.Builder builder() {
            return new Builder();
        }

        /**
         * Set the name of the select parameter.
         *
         * @param name the name of the select parameter.
         * @return the builder to carry on building the column.
         */
        public SelectParameter.Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the defaultValue of the select parameter.
         *
         * @param defaultValue the default value of the select parameter.
         * @return the builder to carry on building the column.
         */
        public SelectParameter.Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Set the implicit of the select parameter.
         *
         * @param implicit true if the parameter is implicit.
         * @return the builder to carry on building the column.
         */
        public SelectParameter.Builder implicit(boolean implicit) {
            this.implicit = implicit;
            return this;
        }

        /**
         * Set the canBeBlank of the select parameter.
         *
         * @param canBeBlank true if the parameter is implicit.
         * @return the builder to carry on building the column.
         */
        public SelectParameter.Builder canBeBlank(boolean canBeBlank) {
            this.canBeBlank = canBeBlank;
            return this;
        }

        /**
         * Add an item to the select parameter builder.
         *
         * @param name the item name.
         * @param value the item value.
         * @param parameter the item optional parameter.
         * @return the builder to carry on building the column.
         */
        public SelectParameter.Builder item(String name, String value, Parameter parameter) {
            this.items.add(new Item(name, value, parameter));
            return this;
        }

        /**
         * Add an item to the select parameter builder.
         *
         * @param name the item name.
         * @param value the item value.
         * @return the builder to carry on building the column.
         */
        public SelectParameter.Builder item(String name, String value) {
            this.items.add(new Item(name, value, null));
            return this;
        }

        /**
         * Build the column with the previously entered values.
         *
         * @return the built column metadata.
         */
        public SelectParameter build() {
            return new SelectParameter(name, defaultValue, implicit, canBeBlank, items, multiple);
        }

    }

}
