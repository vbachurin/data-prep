package org.talend.dataprep.transformation.api.action.metadata.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stef on 16/12/15.
 */
@Component
public class RegexParametersHelper {

    /** The dataprep ready jackson builder. */
    @Autowired
    @Lazy
    // needed to prevent a circular dependency
    public Jackson2ObjectMapperBuilder builder;

    /** The regex mode parameter name. */
    public static final String REGEX_MODE = "regex";

    /** The value of the 'equals' operator. */
    public static final String EQUALS_MODE = "equals";

    /** The value of the 'contains' operator. */
    public static final String CONTAINS_MODE = "contains";

    /** The starts with parameter name. */
    public static final String STARTS_WITH_MODE = "starts_with";

    /** The ends with parmeter name. */
    public static final String ENDS_WITH_MODE = "ends_with";

    public static final ReplaceOnValueParameter EMPTY = new ReplaceOnValueParameter("", EQUALS_MODE);

    public String getEmptyParamAsString() {
/*        try {
            return builder.build().writeValueAsString(EMPTY);
        } catch (JsonProcessingException e) {
            return "";
        }*/
        return "";
    }

    public ReplaceOnValueParameter build(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            throw new InvalidParameterException(jsonString + " is not a valid json");
        }
        try {
            return builder.build().readValue(jsonString, ReplaceOnValueParameter.class);
        } catch (IOException e) {
            throw new InvalidParameterException(e.getMessage());
        }
    }

    /**
     * Class used to simplify the json parsing of the parameter for this action.
     */
    public static class ReplaceOnValueParameter {

        /** The token. */
        private String token;

        /** The operator. */
        private String operator;

        /** The pattern, used only in regex mode. */
        private Pattern pattern;

        /**
         * Constructor.
         *
         * @param token the token.
         * @param operator the operator.
         */
        @JsonCreator
        public ReplaceOnValueParameter(@JsonProperty("token") String token, @JsonProperty("operator") String operator) {
            this.token = token;
            this.operator = operator;
        }

        /**
         * @return the Token
         */
        public String getToken() {
            return token;
        }

        /**
         * @return the Operator
         */
        public String getOperator() {
            return operator;
        }

        public boolean isValid() {
            // regex validity check
            final Boolean regexMode = this.operator.equals(REGEX_MODE);

            if (regexMode && pattern == null) {
                String actualPattern = ".*" + this.token + ".*";
                try {
                    pattern = Pattern.compile(actualPattern);
                } catch (Exception e) {
                    return false;
                }
            }

            return true;
        }

        public boolean matches(String value) {
            if (this.token == null || this.token.length() == 0) {
                return false;
            }

            if (!isValid()) {
                return false;
            }
            
            boolean matches = false;
            switch (this.getOperator()) {
            case EQUALS_MODE:
                matches = value.equals(this.getToken());
                break;
            case CONTAINS_MODE:
                matches = value.contains(this.getToken());
                break;
            case STARTS_WITH_MODE:
                matches = value.startsWith(this.getToken());
                break;
            case ENDS_WITH_MODE:
                matches = value.endsWith(this.getToken());
                break;
            case REGEX_MODE:
                final Matcher matcher = pattern.matcher(value);
                matches = matcher.matches();
                break;
            }

            return matches;
        }

    }

}
