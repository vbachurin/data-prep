package org.talend.dataprep.dataset.objects;

import org.talend.dataprep.dataset.service.analysis.schema.FormatGuess;

import java.util.LinkedList;
import java.util.List;

public class DataSetContent {

    private int                     nbRecords;

    private int                     nbLinesInHeader;

    private int                     nbLinesInFooter;

    private final List<FormatGuess> contentTypes = new LinkedList<>();

    private FormatGuess             contentType;

    public void setContentTypeCandidates(List<FormatGuess> guessList) {
        contentTypes.clear();
        contentTypes.addAll(guessList);
    }

    public FormatGuess getContentType() {
        return contentType;
    }

    public void setContentType(FormatGuess contentType) {
        this.contentType = contentType;
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
