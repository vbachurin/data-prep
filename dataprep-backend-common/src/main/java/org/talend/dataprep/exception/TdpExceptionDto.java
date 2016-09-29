package org.talend.dataprep.exception;

import java.util.List;
import java.util.Map;

import org.talend.daikon.exception.error.ErrorCode;
import org.talend.dataprep.exception.error.ErrorMessage;

import static java.util.stream.Collectors.toList;

/**
 * Representation of an exception for the APIs.
 */
public class TdpExceptionDto {

    private final String code;

    private final String message;

    private final String messageTitle;

    private final Map<String, Object> context;

    private final String cause;

    /**
     * Creates the DTO based on a {@link TDPException}. It handles the conversion code that would be serialization code.
     *
     * @param internal the internal form of {@link TDPException}.
     * @return the {@link TdpExceptionDto} ready to be serialized to external products
     */
    public static TdpExceptionDto from(TDPException internal) {
        ErrorCode errorCode = internal.getCode();
        String code = errorCode.getProduct() + '_' + errorCode.getGroup() + '_' + errorCode.getCode();

        String[] values = getContextValuesStringArray(internal);
        String message = ErrorMessage.getMessage(errorCode, values);
        String messageTitle = ErrorMessage.getMessageTitle(errorCode, values);

        String cause = internal.getCause() == null ? null : internal.getCause().getMessage();
        Map<String, Object> context = internal.getContextAsMap();

        return new TdpExceptionDto(code, cause, message, messageTitle, context);
    }

    private static String[] getContextValuesStringArray(TDPException internal) {
        List<String> contextValuesAsString = internal.getContextAsMap().values().stream().map(Object::toString).collect(toList());
        return contextValuesAsString.toArray(new String[contextValuesAsString.size()]);
    }

    public TdpExceptionDto(String code, String cause, String message, String messageTitle, Map<String, Object> context) {
        this.code = code;
        this.cause = cause;
        this.message = message;
        this.messageTitle = messageTitle;
        this.context = context;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public String getCause() {
        return cause;
    }
}
