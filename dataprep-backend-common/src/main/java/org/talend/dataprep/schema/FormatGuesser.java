package org.talend.dataprep.schema;

import java.util.Map;

/**
 * Represents a class able to create {@link org.talend.dataprep.schema.FormatGuess} from a data set content.
 */
public interface FormatGuesser {

    /**
     * Implement this method in case the FormatGuesser implementation:
     * <ul>
     *     <li>Only support a fixed set of encodings</li>
     *     <li>Auto detect encoding (in that case, accept only one encoding, e.g. UTF-8).</li>
     * </ul>
     *
     * @param encoding A encoding as returned by {@link java.nio.charset.Charset#forName(String)}.
     * @return <code>true</code> if <code>encoding</code> can be used in {@link #guess(SchemaParser.Request, String)}
     * @see #guess(SchemaParser.Request, String)
     */
    boolean accept(String encoding);

    /**
     * Guess the content type of the provided stream.
     *
     * @param request The Schema parser request. Content cannot be <code>null</code>.
     * @param encoding The encoding to use to read content in <code>stream</code>.
     * @return A {@link org.talend.dataprep.schema.FormatGuess guess} that can never be null (see
     * {@link FormatGuess#getConfidence()}.
     * @throws IllegalArgumentException If stream is <code>null</code>.
     */
    Result guess(SchemaParser.Request request, String encoding);

    /**
     * Format guess result.
     */
    class Result {

        /** The format guess. */
        private FormatGuess formatGuess;

        /** The parameters (e.g. separator for CSV). */
        private Map<String, String> parameters;

        /** The encoding. */
        private String encoding;

        /**
         * Constructor.
         *
         * @param formatGuess the format guess.
         * @param encoding the encoding to use.
         * @param parameters the needed parameters.
         */
        public Result(FormatGuess formatGuess, String encoding, Map<String, String> parameters) {
            this.formatGuess = formatGuess;
            this.encoding = encoding;
            this.parameters = parameters;
        }

        /**
         * @return the FormatGuess.
         */
        public FormatGuess getFormatGuess() {
            return formatGuess;
        }

        /**
         * @param formatGuess the formatGuess to set.
         */
        public void setFormatGuess(FormatGuess formatGuess) {
            this.formatGuess = formatGuess;
        }

        /**
         * @return the Parameters.
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * @param parameters the parameters to set.
         */
        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }

        /**
         * @return the Encoding.
         */
        public String getEncoding() {
            return encoding;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Result)) {
                return false;
            }

            Result result = (Result) o;

            if (!formatGuess.equals(result.formatGuess)) {
                return false;
            }
            return parameters.equals(result.parameters);

        }

        @Override
        public int hashCode() {
            int result = formatGuess.hashCode();
            result = 31 * result + parameters.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Result{" + "formatGuess=" + formatGuess != null ? formatGuess.getClass().getSimpleName()
                    : "null" + ", encoding='" + encoding + '\'' + '}';
        }
    }
}
