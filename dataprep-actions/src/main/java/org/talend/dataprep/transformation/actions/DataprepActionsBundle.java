package org.talend.dataprep.transformation.actions;

import org.talend.dataprep.i18n.AbstractBundle;
import org.talend.dataprep.i18n.DataprepBundle;

public class DataprepActionsBundle extends AbstractBundle {

    private static final DataprepActionsBundle INSTANCE = new DataprepActionsBundle();

    private DataprepActionsBundle() {
        super(new String[] {}, DataprepBundle.getDataprepBundle());
    }

    /**
     * Get an internationalized message from the dataprep action message bundle.
     *
     * @param key    the message key.
     * @param params the message parameters.
     * @return the internationalized message.
     * @see org.springframework.context.MessageSource
     */
    public static String message(String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    /**
     * {@link #message(String, Object...)} but with a default value if the key does not match any message.
     *
     * @deprecated There should never need a default value as the translation is packaged in the jar and should be tested.
     */
    @Deprecated
    public static String messageWithDefault(String key, Object[] params, String defaultValue) {
        String message = DataprepBundle.message(key);
        return key.equals(message) ? defaultValue : message;
    }

    //
    // Shortcuts to access standard messages used in actions and
    //

    public static String parameter(String key, Object... params) {
        return message("parameter." + key, params);
    }

    public static String parameterLabel(String key, Object... params) {
        return parameter(key + ".label", params);
    }

    public static String parameterDescription(String key, Object... params) {
        return parameter(key + ".desc", params);
    }

    public static String choice(String key, Object... params) {
        return message("choice." + key, params);
    }

    public static String action(String key, Object... params) {
        return message("action." + key, params);
    }

    public static String actionLabel(String key, Object... params) {
        return action(key + ".label", params);
    }

    public static String actionDescription(String key, Object... params) {
        return action(key + ".desc", params);
    }

    public static String actionDocumentation(String key, Object... params) {
        return action(key + ".url", params);
    }

    /**
     * Retrieve the singleton instance to use as bundle parent.
     */
    public static DataprepActionsBundle getDataprepBundle() {
        return INSTANCE;
    }

}
