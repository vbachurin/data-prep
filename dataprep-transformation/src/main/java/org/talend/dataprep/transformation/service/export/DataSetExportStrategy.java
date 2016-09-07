// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.transformation.service.export;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Client;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.format.export.ExportFormat;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.services.dataprep.DataSetService;

/**
 * A {@link ExportStrategy strategy} to export a data set, without using a preparation.
 */
@Component
public class DataSetExportStrategy extends StandardExportStrategy {

    @Client
    DataSetService dataSetService;

    @Override
    public int order() {
        return 2;
    }

    @Override
    public boolean accept(ExportParameters parameters) {
        if (parameters == null) {
            return false;
        }
        return parameters.getContent() == null //
                && !StringUtils.isEmpty(parameters.getDatasetId()) //
                && StringUtils.isEmpty(parameters.getPreparationId());
    }

    @Override
    public StreamingResponseBody execute(ExportParameters parameters) {
        final String formatName = parameters.getExportType();
        final ExportFormat format = getFormat(formatName);
        ExportUtils.setExportHeaders(parameters.getExportName(), format);
        return outputStream -> {
            // get the dataset content (in an auto-closable block to make sure it is properly closed)
            try {
                final DataSet dataSet = dataSetService.get(true, parameters.getDatasetId()).call();
                final DataSetGetMetadata dataSetGetMetadata = dataSetService.getMetadata(parameters.getDatasetId());
                // get the actions to apply (no preparation ==> dataset export ==> no actions)
                Configuration configuration = Configuration.builder() //
                        .args(parameters.getArguments()) //
                        .outFilter(rm -> filterService.build(parameters.getFilter(), rm)) //
                        .format(format.getName()) //
                        .volume(Configuration.Volume.SMALL) //
                        .output(outputStream) //
                        .build();
                factory.get(configuration).transform(dataSet, configuration);
            } catch (TDPException e) {
                throw e;
            } catch (Exception e) {
                throw new TDPException(TransformationErrorCodes.UNABLE_TO_TRANSFORM_DATASET, e);
            }
        };
    }
}
