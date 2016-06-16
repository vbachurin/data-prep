package org.talend.dataprep.transformation.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.cache.ContentCache;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.preparation.store.PreparationRepository;
import org.talend.dataprep.transformation.cache.TransformationCacheKey;
import org.talend.dataprep.transformation.cache.TransformationMetadataCacheKey;

import javax.annotation.Resource;

import java.io.OutputStream;

import static org.junit.Assert.*;

public class OptimizedExportStrategyTest extends TransformationServiceBaseTests {

    /** The root step. */
    @Resource(name = "rootStep")
    private Step rootStep;

    @Autowired
    OptimizedExportStrategy optimizedExportStrategy;

    @Autowired
    PreparationRepository preparationRepository;

    @Autowired
    ContentCache contentCache;

    @Test
    public void testAcceptNullParameters() throws Exception {
        assertFalse(optimizedExportStrategy.accept(null));
    }

    @Test
    public void testAcceptKO_withContent() throws Exception {
        // Given
        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setContent(new DataSet());

        // Then
        assertFalse(optimizedExportStrategy.accept(exportParameters));
    }

    @Test
    public void testAcceptKO_noPreparation() throws Exception {
        // Given
        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setDatasetId("1234");

        // Then
        assertFalse(optimizedExportStrategy.accept(exportParameters));
    }

    @Test(expected = TDPException.class)
    public void testAcceptKO_preparationNotExist() throws Exception {
        // Given
        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId("1234");

        // Then
        optimizedExportStrategy.accept(exportParameters);
    }

    @Test
    public void testAcceptKO_noStepId() throws Exception {
        // Given
        preparationRepository.add(new Preparation("prep-1234", "1234", rootStep.id(), "0.1"));
        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId("prep-1234");

        // Then
        assertFalse(optimizedExportStrategy.accept(exportParameters));
    }

    @Test
    public void testAcceptKO_noMetadataCache() throws Exception {
        // Given
        final String preparation = createEmptyPreparationFromDataset("1234", "test");
        applyAction(preparation, "{}");
        applyAction(preparation, "{}");

        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId(preparation);

        // Then
        assertFalse(optimizedExportStrategy.accept(exportParameters));
    }

    @Test
    public void testAcceptKO_withMetadataCacheNoContentCache() throws Exception {
        // Given
        final String preparation = createEmptyPreparationFromDataset("1234", "test");
        applyAction(preparation, "{}");
        applyAction(preparation, "{}");

        final Preparation preparationDetails = getPreparation(preparation);
        for (String step : preparationDetails.getSteps()) {
            try (OutputStream content = contentCache.put(new TransformationMetadataCacheKey(preparation, step), ContentCache.TimeToLive.DEFAULT)) {
                content.write("{}".getBytes());
                content.flush();
            }
        }

        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId(preparation);

        // Then
        assertFalse(optimizedExportStrategy.accept(exportParameters));
    }

    @Test
    public void testAcceptOK() throws Exception {
        // Given
        final String datasetId = "1234";
        final String format = "";
        final String preparation = createEmptyPreparationFromDataset(datasetId, "test");
        applyAction(preparation, "{}");
        applyAction(preparation, "{}");

        final Preparation preparationDetails = getPreparation(preparation);
        for (String step : preparationDetails.getSteps()) {
            try (OutputStream content = contentCache.put(new TransformationMetadataCacheKey(preparation, step), ContentCache.TimeToLive.DEFAULT)) {
                content.write("{}".getBytes());
                content.flush();
            }
            try (OutputStream content = contentCache.put(new TransformationCacheKey(preparation, datasetId, format, step), ContentCache.TimeToLive.DEFAULT)) {
                content.write("{}".getBytes());
                content.flush();
            }
        }

        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId(preparation);
        exportParameters.setDatasetId(datasetId);
        exportParameters.setExportType(format);

        // Then
        assertTrue(optimizedExportStrategy.accept(exportParameters));
    }

    @Test
    public void testExecute() throws Exception {
        // Given
        final String datasetId = "1234";
        final String format = "JSON";
        final String preparation = createEmptyPreparationFromDataset(datasetId, "test");
        applyAction(preparation, "{}");
        applyAction(preparation, "{}");

        final Preparation preparationDetails = getPreparation(preparation);
        for (String step : preparationDetails.getSteps()) {
            try (OutputStream content = contentCache.put(new TransformationMetadataCacheKey(preparation, step), ContentCache.TimeToLive.DEFAULT)) {
                content.write("{}".getBytes());
                content.flush();
            }
            try (OutputStream content = contentCache.put(new TransformationCacheKey(preparation, datasetId, format, step), ContentCache.TimeToLive.DEFAULT)) {
                content.write("{\"records\": [{\"0000\": \"a\"}]}".getBytes());
                content.flush();
            }
        }

        ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId(preparation);
        exportParameters.setDatasetId(datasetId);
        exportParameters.setExportType(format);

        // Then
        final StreamingResponseBody execute = optimizedExportStrategy.execute(exportParameters);
        execute.writeTo(System.out);
    }
}