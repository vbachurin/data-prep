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

package org.talend.dataprep.transformation.service;

import static java.util.stream.Collectors.toList;
import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.transformation.actions.category.ScopeCategory.COLUMN;
import static org.talend.dataprep.transformation.actions.category.ScopeCategory.LINE;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.client.ClientService;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.format.export.ExportFormat;
import org.talend.dataprep.security.SecurityProxy;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;
import org.talend.dataprep.transformation.aggregation.AggregationService;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.api.action.dynamic.DynamicType;
import org.talend.dataprep.transformation.api.action.dynamic.GenericParameter;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.api.transformer.configuration.PreviewConfiguration;
import org.talend.dataprep.transformation.api.transformer.suggestion.Suggestion;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngine;
import org.talend.dataprep.transformation.format.JsonFormat;
import org.talend.dataprep.transformation.preview.api.PreviewParameters;
import org.talend.services.dataprep.DataSetService;
import org.talend.services.dataprep.TransformationService;

import com.fasterxml.jackson.core.JsonParser;

@ServiceImplementation
public class TransformationServiceImpl extends BaseTransformationService implements TransformationService {

    /**
     * This class' logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TransformationService.class);

    @Autowired
    ClientService clientService;

    /**
     * The Spring application context.
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * All available transformation actions.
     */
    @Autowired
    private ActionMetadata[] allActions;

    /**
     * he aggregation service.
     */
    @Autowired
    private AggregationService aggregationService;

    /**
     * The action suggestion engine.
     */
    @Autowired
    private SuggestionEngine suggestionEngine;

    /**
     * The transformer factory.
     */
    @Autowired
    private TransformerFactory factory;

    /**
     * Task executor for asynchronous processing.
     */
    @Resource(name = "serializer#json#executor")
    private TaskExecutor executor;

    /**
     * Security proxy enable a thread to borrow the identity of another user.
     */
    @Autowired
    private SecurityProxy securityProxy;

    @Autowired
    private ActionParser actionParser;

    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    private ContentCache contentCache;

    /**
     * The root step.
     */
    @Resource(name = "rootStep")
    private Step rootStep;

    @Override
    public StreamingResponseBody execute(ExportParameters parameters) {
        return executeSampleExportStrategy(parameters);
    }

    @Override
    public StreamingResponseBody applyOnDataset(String preparationId, String datasetId, String formatName, String stepId,
            String name, final Map<String, String> exportParams) {
        //@formatter:on
        final ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId(preparationId);
        exportParameters.setDatasetId(datasetId);
        exportParameters.setExportType(formatName);
        exportParameters.setStepId(stepId);
        exportParameters.setExportName(name);
        exportParameters.getArguments().putAll(exportParams);

        return executeSampleExportStrategy(exportParameters);
    }

    @Override
    public StreamingResponseBody exportDataset(String datasetId, String formatName, String name,
            final Map<String, String> exportParams) {
        return applyOnDataset(null, datasetId, formatName, null, name, exportParams);
    }

    @Override
    public AggregationResult aggregate(String rawParams) {
        // parse the aggregation parameters
        final AggregationParameters parameters;
        try {
            parameters = mapper.readerFor(AggregationParameters.class).readValue(rawParams);
            LOG.debug("Aggregation requested {}", parameters);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.BAD_AGGREGATION_PARAMETERS, e);
        }

        InputStream contentToAggregate;

        // get the content of the preparation (internal call with piped streams)
        if (StringUtils.isNotBlank(parameters.getPreparationId())) {
            try {
                PipedOutputStream temp = new PipedOutputStream();
                contentToAggregate = new PipedInputStream(temp);

                // because of piped streams, processing must be asynchronous
                Runnable r = () -> {
                    try {
                        final ExportParameters exportParameters = new ExportParameters();
                        exportParameters.setPreparationId(parameters.getPreparationId());
                        exportParameters.setDatasetId(parameters.getDatasetId());
                        if (parameters.getFilter() != null) {
                            exportParameters.setFilter(mapper.readTree(parameters.getFilter()));
                        }
                        exportParameters.setExportType(JsonFormat.JSON);
                        exportParameters.setStepId(parameters.getStepId());

                        final StreamingResponseBody body = executeSampleExportStrategy(exportParameters);
                        body.writeTo(temp);
                    } catch (IOException e) {
                        throw new TDPException(CommonErrorCodes.UNABLE_TO_AGGREGATE, e);
                    }
                };
                executor.execute(r);
            } catch (IOException e) {
                throw new TDPException(CommonErrorCodes.UNABLE_TO_AGGREGATE, e);
            }
        } else {
            contentToAggregate = clientService.of(DataSetService.class).get(true, parameters.getDatasetId());;
        }

        // apply the aggregation
        try (JsonParser parser = mapper.getFactory().createParser(contentToAggregate)) {
            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
            return aggregationService.aggregate(parameters, dataSet);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        } finally {
            // don't forget to release the connection
            if (contentToAggregate != null) {
                try {
                    contentToAggregate.close();
                } catch (IOException e) {
                    LOG.warn("Could not close dataset input stream while aggregating", e);
                }
            }
        }
    }

    @Override
    public void transformPreview(PreviewParameters previewParameters, final OutputStream output) {

    private void executeDiffOnSample(final PreviewParameters previewParameters, final OutputStream output) {
        final TransformationMetadataCacheKey metadataKey = cacheKeyGenerator.generateMetadataKey(
                previewParameters.getPreparationId(),
                rootStep.id(),
                previewParameters.getSourceType()
        );

        final ContentCacheKey contentKey = cacheKeyGenerator.generateContentKey(
                previewParameters.getDataSetId(),
                previewParameters.getPreparationId(),
                rootStep.id(),
                JSON,
                previewParameters.getSourceType()
        );

        try(final InputStream metadata = contentCache.get(metadataKey);
            final InputStream content = contentCache.get(contentKey);
            final JsonParser contentParser = mapper.getFactory().createParser(content)) {

            // build metadata
            final RowMetadata rowMetadata = mapper.readerFor(RowMetadata.class).readValue(metadata);
            final DataSetMetadata dataSetMetadata = new DataSetMetadata();
            dataSetMetadata.setRowMetadata(rowMetadata);

            // build dataset
            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(contentParser);
            dataSet.setMetadata(dataSetMetadata);

            // trigger diff
            executePreview( //
                    previewParameters.getNewActions(), //
                    previewParameters.getBaseActions(),  //
                    previewParameters.getTdpIds(), //
                    dataSet, //
                    output //
            );
        } catch (final IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PERFORM_PREVIEW, e);
        }
    }

    private void executeDiffOnDataset(final PreviewParameters previewParameters, final OutputStream output) {
        // because of dataset records streaming, the dataset content must be within an auto closeable block

        boolean identityReleased = false;
        securityProxy.asTechnicalUser();
        try {

            securityProxy.releaseIdentity();
            identityReleased = true;

            final Callable<DataSet> dataSet = clientService.of(DataSetService.class).get(true, previewParameters.getDataSetId());

            // execute the... preview !
            executePreview(previewParameters.getNewActions(), previewParameters.getBaseActions(), previewParameters.getTdpIds(),
                    dataSet.call(), output);

        } catch (Exception e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PERFORM_PREVIEW, e);
        } finally {
            // make sure the technical identity is released
            if (!identityReleased) {
                securityProxy.releaseIdentity();
            }
        }
    }

    @Override
    public List<StepDiff> getCreatedColumns(List<PreviewParameters> previewParameters) {
        return previewParameters.stream().map(this::getCreatedColumns).collect(toList());
    }

    @Override
    @RequestMapping(value = "/preparation/{preparationId}/cache", method = DELETE)
    @ApiOperation(value = "Evict content entries related to the preparation", notes = "This operation remove content entries related to the preparation.")
    @VolumeMetered
    public void evictCache(@ApiParam(value = "Preparation Id.") @PathVariable(value = "preparationId") final String preparationId) {
        for(final ExportParameters.SourceType sourceType : ExportParameters.SourceType.values()) {
            evictCache(preparationId, sourceType);
        }
    }

    private void evictCache(final String preparationId, final ExportParameters.SourceType sourceType) {
        final ContentCacheKey metadataKey = cacheKeyGenerator.metadataBuilder()
                .preparationId(preparationId)
                .sourceType(sourceType)
                .build();
        final ContentCacheKey contentKey = cacheKeyGenerator.contentBuilder()
                .preparationId(preparationId)
                .sourceType(sourceType)
                .build();
        contentCache.evictMatch(metadataKey);
        contentCache.evictMatch(contentKey);
    }

    /**
     * Compare the results of 2 sets of actions, and return the diff metadata Ex : the created columns ids
     */
    private StepDiff getCreatedColumns(final PreviewParameters previewParameters) {
        boolean identityReleased = false;
        securityProxy.asTechnicalUser();
        try {
            securityProxy.releaseIdentity();
            identityReleased = true;
        final DataSetGetMetadata dataSetGetMetadata = context.getBean(DataSetGetMetadata.class, previewParameters.getDataSetId());
        DataSetMetadata dataSetMetadata = dataSetGetMetadata.execute();
        StepDiff stepDiff;
        TransformationContext context = new TransformationContext();
        if (dataSetGetMetadata.isSuccessfulExecution() && dataSetMetadata != null) {
            RowMetadata metadataBase = dataSetMetadata.getRowMetadata();
            RowMetadata metadataAfter = metadataBase.clone();

            applyActionsOnMetadata(context, metadataBase, previewParameters.getBaseActions());
            applyActionsOnMetadata(context, metadataAfter, previewParameters.getNewActions());

            final DataSet dataSet = clientService.of(DataSetService.class).get(true, previewParameters.getDataSetId()).call();
            dataSet.setRecords(dataSet.getRecords().limit(1));
            metadataAfter.diff(metadataBase);

            List<String> createdColumnIds = metadataAfter.getColumns().stream()
                    .filter(c -> Flag.NEW.getValue().equals(c.getDiffFlagValue()))
                    .map(ColumnMetadata::getId)
                    .collect(Collectors.toList());

            // call diff
            executePreview(previewParameters.getNewActions(), previewParameters.getBaseActions(), null, dataSet, output);

            // extract created columns ids
            final JsonNode node = mapper.readTree(output.toString());
            final JsonNode columnsNode = node.findPath("columns");
            final List<String> createdColumns;
            try (Stream<JsonNode> stream = StreamSupport.stream(columnsNode.spliterator(), false)) {
                createdColumns = stream.filter(col -> "new".equals(col.path("__tdpColumnDiff").asText())) //
                        .map(col -> col.path("id").asText()) //
                        .collect(toList());
            }

            // create/return diff
            final StepDiff diff = new StepDiff();
            diff.setCreatedColumns(createdColumns);
            LOG.debug("{} creates {} columns", previewParameters, diff);
            return diff;
        } catch (Exception e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        } finally {
            // make sure the identity is released !
            if (!identityReleased) {
                securityProxy.releaseIdentity();
            }
            stepDiff = new StepDiff();
            stepDiff.setCreatedColumns(createdColumnIds);
        } else {
            stepDiff = null;
            // maybe throw an exception...
        }
        return stepDiff;
    }

    private void applyActionsOnMetadata(TransformationContext context, RowMetadata metadata, String actionsAsJson) {
        List<Action> actions = actionParser.parse(actionsAsJson);
        ActionContext contextWithMetadata = new ActionContext(context, metadata);
        for (Action action : actions) {
            action.getRowAction().compile(contextWithMetadata);

        }
    }

    /**
     * Execute the preview and write result in the provided output stream
     *
     * @param actions The actions to execute to diff with reference
     * @param referenceActions The reference actions
     * @param indexes The record indexes to diff. If null, it will process all records
     * @param dataSet The dataset (column metadata and records)
     * @param output The output stream where to write the result
     */
    private void executePreview(final String actions, final String referenceActions, final String indexes, final DataSet dataSet,
            final OutputStream output) {
        final PreviewConfiguration configuration = PreviewConfiguration.preview() //
                .withActions(actions) //
                .withIndexes(indexes) //
                .fromReference( //
                        Configuration.builder() //
                                .format(JsonFormat.JSON) //
                                .output(output) //
                                .actions(referenceActions) //
                                .build() //
                ) //
                .build();
        factory.get(configuration).transform(dataSet, configuration);
    }

    @Override
    public GenericParameter dynamicParams(String action, String columnId, InputStream content) {
        final DynamicType actionType = DynamicType.fromAction(action);
        if (actionType == null) {
            final ExceptionContext exceptionContext = build().put("name", action);
            throw new TDPException(TransformationErrorCodes.UNKNOWN_DYNAMIC_ACTION, exceptionContext);
        }
        try (JsonParser parser = mapper.getFactory().createParser(content)) {
            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
            return actionType.getGenerator(context).getParameters(columnId, dataSet);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @Override
    public List<ActionMetadata> columnActions(ColumnMetadata column) {
        return Stream.of(allActions) //
                .filter(action -> action.acceptScope(COLUMN)) //
                .map(am -> column != null ? am.adapt(column) : am) //
                .collect(toList());
    }

    @Override
    public List<ActionMetadata> suggest(ColumnMetadata column, int limit) {
        if (column == null) {
            return Collections.emptyList();
        }

        // look for all actions applicable to the column type
        final List<ActionMetadata> actions;
        try (Stream<ActionMetadata> stream = Stream.of(this.allActions)) {
            actions = stream.filter(am -> am.acceptColumn(column)).collect(toList());
        }
        final List<Suggestion> suggestions = suggestionEngine.score(actions, column);
        return suggestions.stream() //
                .filter(s -> s.getScore() > 0) // Keep only strictly positive score (negative and 0 indicates not
                // applicable)
                .limit(limit) //
                .map(Suggestion::getAction) // Get the action for positive suggestions
                .map(am -> am.adapt(column)) // Adapt default values (e.g. column name)
                .collect(toList());
    }

    @Override
    public List<ActionMetadata> lineActions() {
        try (Stream<ActionMetadata> stream = Stream.of(this.allActions)) {
            return stream //
                    .filter(action -> action.acceptScope(LINE)) //
                    .map(action -> action.adapt(LINE)) //
                    .collect(toList());
        }
    }

    @Override
    public List<ActionMetadata> suggest(DataSet dataSet) {
        return Collections.emptyList();
    }

    @Override
    public Iterable<JsonErrorCodeDescription> listErrors() {
        // need to cast the typed dataset errors into mock ones to use json parsing
        List<JsonErrorCodeDescription> errors = new ArrayList<>(TransformationErrorCodes.values().length);
        for (TransformationErrorCodes code : TransformationErrorCodes.values()) {
            errors.add(new JsonErrorCodeDescription(code));
        }
        return errors;
    }

    @Override
    public List<ExportFormat> exportTypes() {
        return formatRegistrationService.getExternalFormats().stream() //
                .sorted((f1, f2) -> f1.getOrder() - f2.getOrder()) // Enforce strict order.
                .collect(toList());
    }

}
