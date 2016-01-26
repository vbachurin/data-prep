package org.talend.dataprep.schema.csv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.talend.dataprep.api.dataset.DataSetMetadata;

/**
 * Unit test for CSVSchemaUpdater.
 * 
 * @see CSVSchemaUpdater
 */
public class CSVSchemaUpdaterTest {

    @InjectMocks
    private CSVSchemaUpdater updater;

    @Mock
    private CSVFormatUtils csvFormatUtils;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldAccept() throws Exception {
        final DataSetMetadata metadata = DataSetMetadata.Builder.metadata().id("toto").formatGuessId("formatGuess#csv").build();
        assertTrue(updater.accept(metadata));
    }

    @Test
    public void shouldNoAccept() throws Exception {
        final DataSetMetadata metadata = DataSetMetadata.Builder.metadata().id("tata").formatGuessId("formatGuess#xls").build();
        assertFalse(updater.accept(metadata));
    }

    @Test
    public void shouldCallCsvFormatUtils() throws Exception {
        final DataSetMetadata original = DataSetMetadata.Builder.metadata().id("tata").build();
        final DataSetMetadata updated = DataSetMetadata.Builder.metadata().id("toto").build();
        updater.updateSchema(original, updated);
        verify(csvFormatUtils, times(1)).useNewSeparator(original, updated);
    }
}