package org.talend.dataprep.transformation.api.action.metadata.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils class to ease manipulation of parameters of type ParameterType.REGEX.
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

    public ReplaceOnValueParameter build(String jsonString) {
        if (StringUtils.isEmpty(jsonString)) {
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

        /** Indicates if the match is strict or not. */
        private boolean strict = true;

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

        public boolean isStrict() {
            return strict;
        }

        public void setStrict(boolean strict) {
            this.strict = strict;
        }

        public Pattern getPattern() {
            return pattern;
        }

        private boolean isValid() {
            // regex validity check
            final Boolean regexMode = this.operator.equals(REGEX_MODE);

            if (regexMode && pattern == null) {
                String actualPattern = (strict ? this.token : ".*" + this.token + ".*");
                try {
                    pattern = Pattern.compile(actualPattern);
                } catch (Exception e) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Check if a string matches token & operator.
         *
         * If operator is REGEX we use the strict field.
         * If strict=true value must matches the regex, if strict=false a part of value must matches the regex.
         *
         * @param value
         * @return true if value matches the token regarding operator
         */
        public boolean matches(String value) {
            if (value == null) {
                return false;
            }
            if (StringUtils.isEmpty(this.token)) {
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
