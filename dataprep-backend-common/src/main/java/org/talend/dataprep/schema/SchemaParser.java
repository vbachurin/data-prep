package org.talend.dataprep.schema;

import java.io.InputStream;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;

/**
 * Represents a class able to parse a data set content and return a list of {@link ColumnMetadata metadata} out of it.
 */
public interface SchemaParser {

    /**
     * <p>
     * Parses the provided content and extract {@link ColumnMetadata column} information. Implementations are encouraged
     * to returns as fast as possible from this method (possibly without processing the whole <code>content</code>
     * parameter).
     * </p>
     * 
     * @param content The data set content. It should never be <code>null</code>.
     * @param metadata The data set metadata, to be used to retrieve parameters needed to understand format in
     *                 <code>content</code>.
     * @return {@link SchemaParserResult} containing a list of {@link ColumnMetadata metadata}. When no column name/type
     * can be created, implementations are expected to generate names and select
     * {@link org.talend.dataprep.api.type.Type#STRING string} as type.
     */
    SchemaParserResult parse(InputStream content, DataSetMetadata metadata);

}
