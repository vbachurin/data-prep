package org.talend.dataprep.api.dataset;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dataset content summary.
 */

public class DataSetContent implements Serializable {

    @JsonProperty("records")
    private int nbRecords = 0;

    @JsonProperty("nbLinesHeader")
    private int nbLinesInHeader;

    @JsonProperty("nbLinesFooter")
    private int nbLinesInFooter;

    @JsonProperty("type")
    private String mediaType;

    @JsonProperty("parameters")
    private Map<String, String> parameters = new HashMap<>();

    @JsonProperty("formatGuess")
    private String formatGuessId;

    /**
     * @return A map that contains additional information about the format (e.g. a separator for a CSV format).
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getFormatGuessId() {
        return formatGuessId;
    }

    public void setFormatGuessId(String formatGuessId) {
        this.formatGuessId = formatGuessId;
    }

    public int getNbRecords() {
        return this.nbRecords;
    }

    public void setNbRecords(int lines) {
        this.nbRecords = lines;
    }

    public int getNbLinesInHeader() {
        return this.nbLinesInHeader;
    }

    public void setNbLinesInHeader(int nbLinesInHeader) {
        this.nbLinesInHeader = nbLinesInHeader;
    }

    public int getNbLinesInFooter() {
        return this.nbLinesInFooter;
    }

    public void setNbLinesInFooter(int nbLinesInFooter) {
        this.nbLinesInFooter = nbLinesInFooter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataSetContent that = (DataSetContent) o;
        return Objects.equals(nbRecords, that.nbRecords) && //
                Objects.equals(nbLinesInHeader, that.nbLinesInHeader) && //
                Objects.equals(nbLinesInFooter, that.nbLinesInFooter) && //
                Objects.equals(mediaType, that.mediaType) && //
                Objects.equals(parameters, that.parameters) && //
                Objects.equals(formatGuessId, that.formatGuessId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nbRecords, nbLinesInHeader, nbLinesInFooter, mediaType, parameters, formatGuessId);
    }
}
