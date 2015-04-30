package org.talend.dataprep.api.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.talend.dataprep.schema.FormatGuess;
import org.talend.dataprep.schema.FormatGuesser;

public class DataSetContent {

    private int nbRecords;

    private int nbLinesInHeader;

    private int nbLinesInFooter;

    private Map<String, String> parameters = new HashMap<>();

    // FIXME is it really used???
    private final List<FormatGuesser.Result> contentTypes = new LinkedList<>();

    private String formatGuessId;

    public void setContentTypeCandidates(List<FormatGuesser.Result> guessList) {
        contentTypes.clear();
        contentTypes.addAll(guessList);
    }

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

    public String getFormatGuessId()
    {
        return formatGuessId;
    }

    public void setFormatGuessId( String formatGuessId )
    {
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

}
