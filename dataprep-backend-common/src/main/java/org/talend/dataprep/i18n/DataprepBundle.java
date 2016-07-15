package org.talend.dataprep.i18n;

/**
 * Singleton to access all dataprep messages. It can be used as parent bundle using {@link #getDataprepBundle()}.
 */
public class DataprepBundle extends AbstractBundle {

    private static final DataprepBundle INSTANCE = new DataprepBundle();

    private DataprepBundle() {
        super(new String[] { "org.talend.dataprep.messages", "org.talend.dataprep.error_messages" });
    }

    /**
     * Get an internationalized message from the dataprep message bundle.
     *
     * @param key    the message key.
     * @param params the message parameters.
     * @return the internationalized message.
     * @see org.springframework.context.MessageSource
     */
    public static String message(String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    public static String messageWithDefault(String key, Object[] params, String defaultValue) {
        String message = DataprepBundle.message(key);
        return key.equals(message) ? defaultValue : message;
    }

    /**
     * Retrieve the singleton instance to use as bundle parent.
     */
    public static DataprepBundle getDataprepBundle() {
        return INSTANCE;
    }
}
