// ============================================================================
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

package org.talend.dataprep.api.service;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.talend.dataprep.command.CommandHelper.toStream;

import java.io.InputStream;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Client;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.action.ActionDefinition;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.service.api.DynamicParamsInput;
import org.talend.dataprep.api.service.command.preparation.PreparationGetContent;
import org.talend.dataprep.api.service.command.transformation.*;
import org.talend.dataprep.command.CommandHelper;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.command.dataset.DataSetGet;
import org.talend.services.dataprep.TransformationService;
import org.talend.services.dataprep.api.TransformAPI;

import com.netflix.hystrix.HystrixCommand;

@ServiceImplementation
public class TransformAPIImpl extends APIService implements TransformAPI {

    @Client
    TransformationService transformationService;

    @Override
    public Stream<ActionDefinition> columnActions(ColumnMetadata columnMetadata) {
        return transformationService.columnActions(columnMetadata);
    }

    @Override
    public Stream<ActionDefinition> suggestColumnActions(ColumnMetadata columnMetadata) {
        return transformationService.suggest(columnMetadata, 5);
    }

    @Override
    public Stream<ActionDefinition> lineActions() {
        return toStream(ActionDefinition.class, mapper, getCommand(LineActions.class));
    }

    @Override
    public ResponseEntity<StreamingResponseBody> suggestActionParams(String action, DynamicParamsInput dynamicParamsInput) {
        // get preparation/dataset content
        HystrixCommand<InputStream> inputData;
        final String preparationId = dynamicParamsInput.getPreparationId();
        if (isNotBlank(preparationId)) {
            inputData = getCommand(PreparationGetContent.class, preparationId, dynamicParamsInput.getStepId());
        } else {
            inputData = getCommand(DataSetGet.class, dynamicParamsInput.getDatasetId(), false, false);
        }

        // get params, passing content in the body
        final GenericCommand<InputStream> getActionDynamicParams = getCommand(SuggestActionParams.class, inputData, action,
                dynamicParamsInput.getColumnId());
        return CommandHelper.toStreaming(getActionDynamicParams);
    }

    @Override
    public StreamingResponseBody getDictionary() {
        // get preparation/dataset content
        HystrixCommand<InputStream> dictionaryCommand = getCommand(DictionaryCommand.class);
        return CommandHelper.toStreaming(dictionaryCommand);
    }
}
