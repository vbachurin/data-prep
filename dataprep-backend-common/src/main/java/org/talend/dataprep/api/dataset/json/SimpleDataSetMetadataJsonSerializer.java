package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.Quality;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class SimpleDataSetMetadataJsonSerializer {

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-YYYY HH:mm"); //$NON-NLS-1
    static
    {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
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

            if(dataSetMetadata.getContent().getContentType() != null) {
                generator.writeStringField("type", dataSetMetadata.getContent().getContentType().getMediaType()); //$NON-NLS-1
            }

            synchronized (DATE_FORMAT) {
                generator.writeStringField("created", DATE_FORMAT.format(dataSetMetadata.getCreationDate())); //$NON-NLS-1
            }
        }
        generator.writeEndObject();
    }
}
