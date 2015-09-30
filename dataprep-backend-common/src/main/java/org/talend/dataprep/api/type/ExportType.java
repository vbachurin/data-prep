package org.talend.dataprep.api.type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.type.json.ExportTypeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ExportTypeSerializer.class)
public enum ExportType {

    //@formatter:off
    // take care when declaring new export type as only one can be default :-)
    CSV("text/csv", ".csv", true, false, //
        Lists.newArrayList( //
                            new Parameter( "csvSeparator", //
                                           "CHOOSE_SEPARATOR", //
                                           "radio", //
                                           new ParameterValue( ";", "SEPARATOR_SEMI_COLON" ), //
                                           Arrays.asList( //
                                                          new ParameterValue( "\u0009", "SEPARATOR_TAB" ), // &#09;
                                                          new ParameterValue( " ", "SEPARATOR_SPACE" ), //
                                                          new ParameterValue( ",", "SEPARATOR_COMMA" ) //
                                           ) //
                            ), //
                            new Parameter( Parameter.FILENAME_PARAMETER, //
                                           "EXPORT_FILENAME",  //
                                            "text", //
                                            new ParameterValue( StringUtils.EMPTY, "EXPORT_FILENAME_DEFAULT" ), //
                                            Collections.emptyList() //
                            ) //
        ) //
    ), //
    XLS("application/vnd.ms-excel", ".xls", true, true, //
        Collections.singletonList( new Parameter( Parameter.FILENAME_PARAMETER, //
                                           "EXPORT_FILENAME",  //
                                           "text", //
                                           new ParameterValue( StringUtils.EMPTY, "EXPORT_FILENAME_DEFAULT" ), //
                                           Collections.emptyList() //
                            ) //
        ) //
    ), //
    TABLEAU("application/tde", ".tde", false, false, Collections.<Parameter> emptyList()), //
    JSON("application/json", ".json", false, false, Collections.<Parameter> emptyList()); //
    //@formatter:on

    /** The mime type. */
    private final String mimeType;

    /** The file extension. */
    private final String extension;

    /** Does this export type need more parameters? (ui will open a new form in this case). */
    private final boolean needParameters;

    /** Is it the default export. */
    private final boolean defaultExport;

    /** List of extra parameters needed for this export (i.e separator for csv files etc...). */
    private final List<Parameter> parameters;

    /**
     * Default constructor.
     *
     * @param mimeType the export mime type.
     * @param extension the file extension.
     * @param needParameters if the type needs parameters.
     * @param defaultExport if it's the default export.
     * @param parameters the list of parameters.
     */
    ExportType(final String mimeType, final String extension, final boolean needParameters, final boolean defaultExport,
            final List<Parameter> parameters) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.needParameters = needParameters;
        this.defaultExport = defaultExport;
        this.parameters = parameters;
    }

    /**
     * @return the mime type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return the file extension.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return true if parameters are needed.
     */
    public boolean isNeedParameters() {
        return needParameters;
    }

    /**
     * @return true if it's the default export.
     */
    public boolean isDefaultExport() {
        return defaultExport;
    }

    /**
     * @return the list of needed parameters.
     */
    public List<Parameter> getParameters() {
        return parameters;
    }


    /**
     * Inner Parameter class.
     */
    public static class Parameter implements Serializable {

        public static final String FILENAME_PARAMETER = "fileName";

        /** Parameter name. */
        private final String name;

        /** Can be used as a label key in the ui. */
        private final String labelKey;

        /** The default value for the parameter must not be in values. */
        private final ParameterValue defaultValue;

        /** All possible values for the parameter. */
        private final List<ParameterValue> values;

        /** Html type (input type: radio, text). */
        private final String type;

        /**
         * Constructor.
         *
         * param name the parameter name.
         * 
         * @param labelKey the label key that may be used by the ui.
         * @param type the parameter html type.
         * @param defaultValue the parameter default value.
         * @param values the parameters values.
         */
        public Parameter(String name, String labelKey, String type, ParameterValue defaultValue, List<ParameterValue> values) {
            this.name = name;
            this.labelKey = labelKey;
            this.defaultValue = defaultValue;
            this.values = values;
            this.type = type;
        }

        /**
         * @return the parameter name.
         */
        public String getName() {
            return name;
        }

        /**
         * @return the label to use by the UI.
         */
        public String getLabelKey() {
            return labelKey;
        }

        /**
         * @return the default value.
         */
        public ParameterValue getDefaultValue() {
            return defaultValue;
        }

        /**
         * @return the list of parameters value.
         */
        public List<ParameterValue> getValues() {
            return values;
        }

        /**
         * @return the type.
         */
        public String getType() {
            return type;
        }
    }

    /**
     * Inner class for parameter value.
     */
    public static class ParameterValue {

        /** The parameter value. */
        private final String value;

        /** The label to use by the UI. */
        private final String labelKey;

        /**
         * Constructor.
         *
         * @param value the parameter value.
         * @param labelKey the label to use by the UI.
         */
        public ParameterValue(String value, String labelKey) {
            this.value = value;
            this.labelKey = labelKey;
        }

        /**
         * @return the parameter value.
         */
        public String getValue() {
            return value;
        }

        /**
         * @return the label to use by the UI.
         */
        public String getLabelKey() {
            return labelKey;
        }
    }

}
