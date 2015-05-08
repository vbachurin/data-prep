package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.schema.FormatGuess;

import com.fasterxml.jackson.core.JsonGenerator;

@Component
public class SimpleDataSetMetadataJsonSerializer {

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-YYYY HH:mm"); //$NON-NLS-1
    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private ApplicationContext applicationContext;

    @Autowired
    public SimpleDataSetMetadataJsonSerializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * <p>
     * Write "general" information about the <code>dataSetMetadata</code> to the JSON <code>generator</code>. General
     * information covers:
     * <ul>
     * <li>Id: see {@link DataSetMetadata#getId()}</li>
     * <li>Name: see {@link DataSetMetadata#getName()}</li>
     * <li>Author: see {@link DataSetMetadata#getAuthor()}</li>
     * <li>Date of creation: see {@link DataSetMetadata#getCreationDate()}</li>
     * </ul>
     * </p>
     * <p>
     * <b>Note:</b> this method only writes fields, callers are expected to start a JSON Object to hold values.
     * </p>
     *
     * @param dataSetMetadata The {@link DataSetMetadata metadata} to get information from.
     * @param generator The JSON generator this methods writes to.
     * @throws IOException In case method can't successfully write to <code>generator</code>.
     */
    public void serialize(final DataSetMetadata dataSetMetadata, final JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        {
            generator.writeStringField("id", dataSetMetadata.getId()); //$NON-NLS-1
            generator.writeStringField("name", dataSetMetadata.getName()); //$NON-NLS-1
            generator.writeStringField("author", dataSetMetadata.getAuthor()); //$NON-NLS-1
            generator.writeNumberField("records", dataSetMetadata.getContent().getNbRecords()); //$NON-NLS-1
            generator.writeNumberField("nbLinesHeader", dataSetMetadata.getContent().getNbLinesInHeader()); //$NON-NLS-1
            generator.writeNumberField("nbLinesFooter", dataSetMetadata.getContent().getNbLinesInFooter()); //$NON-NLS-1
            generator.writeBooleanField("draft", dataSetMetadata.isDraft()); //$NON-NLS-1
            generator.writeStringField("certification", dataSetMetadata.getGovernance().getCertificationStep().toString()); //$NON-NLS-1
            if (dataSetMetadata.getContent().getFormatGuessId() != null) {
                FormatGuess formatGuess = applicationContext.getBean(dataSetMetadata.getContent().getFormatGuessId(), //
                        FormatGuess.class);

                generator.writeStringField("type", formatGuess.getMediaType()); //$NON-NLS-1

            }

            // data we need for extra dataset validation (i.e sheetNumber for excell sheet)
            if (dataSetMetadata.getSchemaParserResult() != null) {
                generator.writeStringField("sheetName", dataSetMetadata.getSchemaParserResult()
                        .getSheetName());
            }

            synchronized (DATE_FORMAT) {
                generator.writeStringField("created", DATE_FORMAT.format(dataSetMetadata.getCreationDate())); //$NON-NLS-1
            }
        }
        generator.writeEndObject();
    }
}
