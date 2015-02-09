package org.talend.dataprep.api;

import java.util.LinkedList;
import java.util.List;

import org.talend.dataprep.dataset.service.analysis.schema.FormatGuess;

public class DataSetContent {

    private int                     lines;

    private final List<FormatGuess> contentTypes = new LinkedList<>();

    private FormatGuess contentType;

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

    public int getLines() {
        return this.lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

}
