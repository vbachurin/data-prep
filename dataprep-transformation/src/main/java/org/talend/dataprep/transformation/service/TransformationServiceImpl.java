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

package org.talend.dataprep.transformation.service;

import static java.util.Collections.singletonList;
import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.api.export.ExportParameters.SourceType.HEAD;
import static org.talend.dataprep.exception.error.PreparationErrorCodes.PREPARATION_DOES_NOT_EXIST;
import static org.talend.dataprep.exception.error.TransformationErrorCodes.UNEXPECTED_EXCEPTION;
import static org.talend.dataprep.quality.AnalyzerService.Analysis.SEMANTIC;
import static org.talend.dataprep.transformation.actions.category.ScopeCategory.COLUMN;
import static org.talend.dataprep.transformation.actions.category.ScopeCategory.LINE;
import static org.talend.dataprep.transformation.format.JsonFormat.JSON;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Resource;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.action.ActionDefinition;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.Flag;
import org.talend.dataprep.api.dataset.statistics.SemanticDomain;
import org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.cache.ContentCache;
import org.talend.dataprep.cache.ContentCacheKey;
import org.talend.dataprep.command.dataset.DataSetGet;
import org.talend.dataprep.command.dataset.DataSetGetMetadata;
import org.talend.dataprep.command.preparation.PreparationDetailsGet;
import org.talend.dataprep.conversions.BeanConversionService;
import org.talend.dataprep.dataset.StatisticsAdapter;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.format.export.ExportFormat;
import org.talend.dataprep.format.export.ExportFormatMessage;
import org.talend.dataprep.quality.AnalyzerService;
import org.talend.dataprep.security.SecurityProxy;
import org.talend.dataprep.transformation.actions.common.RunnableAction;
import org.talend.dataprep.transformation.aggregation.AggregationService;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.dynamic.DynamicType;
import org.talend.dataprep.transformation.api.action.dynamic.GenericParameter;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.api.transformer.configuration.PreviewConfiguration;
import org.talend.dataprep.transformation.api.transformer.suggestion.Suggestion;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngine;
import org.talend.dataprep.transformation.cache.CacheKeyGenerator;
import org.talend.dataprep.transformation.cache.TransformationMetadataCacheKey;
import org.talend.dataprep.transformation.pipeline.ActionRegistry;
import org.talend.dataprep.transformation.preview.api.PreviewParameters;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.semantic.broadcast.BroadcastIndexObject;
import org.talend.services.dataprep.TransformationService;

import com.fasterxml.jackson.core.JsonParser;

@ServiceImplementation
public class TransformationServiceImpl extends BaseTransformationService implements TransformationService {

    /**
     * This class' logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TransformationServiceImpl.class);

    @Autowired
    private AnalyzerService analyzerService;

    /**
     * The Spring application context.
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * All available transformation actions.
     */
    @Autowired
    private ActionRegistry actionRegistry;

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

    @Autowired
    private BeanConversionService beanConversionService;

    @Autowired
    private StatisticsAdapter statisticsAdapter;

    /**
     * The root step.
     */
    @Resource(name = "rootStep")
    private Step rootStep;

    @Override
    public StreamingResponseBody execute(final ExportParameters parameters) {
        return executeSampleExportStrategy(parameters);
    }

    @Override
    public StreamingResponseBody applyOnDataset(String preparationId, String datasetId, String formatName, String stepId,
            String name, Map<String, String> exportParams) {
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
            Map<String, String> exportParams) {
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
                        exportParameters.setExportType(JSON);
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
            final DataSetGet dataSetGet = context.getBean(DataSetGet.class, parameters.getDatasetId(), false, true);
            contentToAggregate = dataSetGet.execute();
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
    public StreamingResponseBody transformPreview(PreviewParameters previewParameters) {
        //@formatter:on
        if (shouldApplyDiffToSampleSource(previewParameters)) {
            return output -> executeDiffOnSample(previewParameters, output);
        } else {
            return output -> executeDiffOnDataset(previewParameters, output);
        }
    }

    private void executeDiffOnSample(final PreviewParameters previewParameters, final OutputStream output) {
        final TransformationMetadataCacheKey metadataKey = cacheKeyGenerator.generateMetadataKey( //
                previewParameters.getPreparationId(), //
                rootStep.id(), //
                previewParameters.getSourceType() //
        );

        final ContentCacheKey contentKey = cacheKeyGenerator.generateContentKey( //
                previewParameters.getDataSetId(), //
                previewParameters.getPreparationId(), //
                rootStep.id(), //
                JSON, //
                previewParameters.getSourceType() //
        );

        try (final InputStream metadata = contentCache.get(metadataKey); //
                final InputStream content = contentCache.get(contentKey); //
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
                    previewParameters.getBaseActions(), //
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
        final DataSetGet dataSetGet = context.getBean(DataSetGet.class, previewParameters.getDataSetId(), false, true);

        boolean identityReleased = false;
        securityProxy.asTechnicalUser();
        try (final InputStream dataSetContent = dataSetGet.execute(); //
                final JsonParser parser = mapper.getFactory().createParser(dataSetContent)) {

            securityProxy.releaseIdentity();
            identityReleased = true;

            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
            executePreview( //
                    previewParameters.getNewActions(), //
                    previewParameters.getBaseActions(), //
                    previewParameters.getTdpIds(), //
                    dataSet, //
                    output //
            );

        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PERFORM_PREVIEW, e);
        } finally {
            // make sure the technical identity is released
            if (!identityReleased) {
                securityProxy.releaseIdentity();
            }
        }
    }

    private boolean shouldApplyDiffToSampleSource(final PreviewParameters previewParameters) {
        if (previewParameters.getSourceType() != HEAD && previewParameters.getPreparationId() != null) {
            final TransformationMetadataCacheKey metadataKey = cacheKeyGenerator.generateMetadataKey( //
                    previewParameters.getPreparationId(), //
                    rootStep.id(), //
                    previewParameters.getSourceType() //
            );

            final ContentCacheKey contentKey = cacheKeyGenerator.generateContentKey( //
                    previewParameters.getDataSetId(), //
                    previewParameters.getPreparationId(), //
                    rootStep.id(), //
                    JSON, //
                    previewParameters.getSourceType() //
            );

            return contentCache.has(metadataKey) && contentCache.has(contentKey);
        }
        return false;
    }

    public Stream<StepDiff> getCreatedColumns(List<PreviewParameters> previewParameters) {
        return previewParameters.stream().map(this::getCreatedColumns);
    }

    @Override
    public void evictCache(String preparationId) {
        for (final ExportParameters.SourceType sourceType : ExportParameters.SourceType.values()) {
            evictCache(preparationId, sourceType);
        }
    }

    private void evictCache(final String preparationId, final ExportParameters.SourceType sourceType) {
        final ContentCacheKey metadataKey = cacheKeyGenerator.metadataBuilder().preparationId(preparationId)
                .sourceType(sourceType).build();
        final ContentCacheKey contentKey = cacheKeyGenerator.contentBuilder().preparationId(preparationId).sourceType(sourceType)
                .build();
        contentCache.evictMatch(metadataKey);
        contentCache.evictMatch(contentKey);
    }

    /**
     * Compare the results of 2 sets of actions, and return the diff metadata Ex : the created columns ids
     */
    private StepDiff getCreatedColumns(final PreviewParameters previewParameters) {
        final DataSetGetMetadata dataSetGetMetadata = context.getBean(DataSetGetMetadata.class, previewParameters.getDataSetId());
        DataSetMetadata dataSetMetadata = dataSetGetMetadata.execute();
        StepDiff stepDiff;
        TransformationContext context = new TransformationContext();
        if (dataSetGetMetadata.isSuccessfulExecution() && dataSetMetadata != null) {
            RowMetadata metadataBase = dataSetMetadata.getRowMetadata();
            RowMetadata metadataAfter = metadataBase.clone();

            applyActionsOnMetadata(context, metadataBase, previewParameters.getBaseActions());
            applyActionsOnMetadata(context, metadataAfter, previewParameters.getNewActions());

            metadataAfter.diff(metadataBase);

            List<String> createdColumnIds = metadataAfter.getColumns().stream()
                    .filter(c -> Flag.NEW.getValue().equals(c.getDiffFlagValue())).map(ColumnMetadata::getId)
                    .collect(Collectors.toList());

            stepDiff = new StepDiff();
            stepDiff.setCreatedColumns(createdColumnIds);
        } else {
            stepDiff = null;
            // maybe throw an exception...
        }
        return stepDiff;
    }

    private void applyActionsOnMetadata(TransformationContext context, RowMetadata metadata, String actionsAsJson) {
        List<RunnableAction> actions = actionParser.parse(actionsAsJson);
        ActionContext contextWithMetadata = new ActionContext(context, metadata);
        for (RunnableAction action : actions) {
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
                                .format(JSON) //
                                .output(output) //
                                .actions(referenceActions) //
                                .build() //
                ) //
                .build();
        factory.get(configuration).transform(dataSet, configuration);
    }

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
    public Stream<ActionDefinition> columnActions(ColumnMetadata column) {
        return actionRegistry.findAll() //
                .filter(action -> !"TEST".equals(action.getCategory()) && action.acceptScope(COLUMN)) //
                .map(am -> column != null ? am.adapt(column) : am);
    }

    @Override
    public Stream<ActionDefinition> suggest(ColumnMetadata column, int limit) {
        if (column == null) {
            return Stream.empty();
        }

        // look for all actions applicable to the column type
        final Stream<Suggestion> suggestions = suggestionEngine
                .score(actionRegistry.findAll().parallel().filter(am -> am.acceptField(column)), column);
        return suggestions //
                .filter(s -> s.getScore() > 0) // Keep only strictly positive score (negative and 0 indicates not applicable)
                .limit(limit) //
                .map(Suggestion::getAction) // Get the action for positive suggestions
                .map(am -> am.adapt(column)); // Adapt default values (e.g. column name)
    }

    @Override
    public Stream<ActionDefinition> lineActions() {
        return actionRegistry.findAll() //
                .filter(action -> action.acceptScope(LINE)) //
                .map(action -> action.adapt(LINE));
    }

    @Override
    public List<ActionDefinition> suggest(DataSet dataSet) {
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
    public Stream<ExportFormatMessage> exportTypes() {
        return formatRegistrationService.getExternalFormats() //
                .sorted(Comparator.comparingInt(ExportFormat::getOrder)) // Enforce strict order.
                .map(f -> beanConversionService.convert(f, ExportFormatMessage.class)) //
                .filter(ExportFormatMessage::isEnabled);
    }

    @Override
    public Stream<ExportFormatMessage> getPreparationExportTypesForPreparation(String preparationId) {
        final Preparation preparation = getPreparation(preparationId);
        final DataSetMetadata metadata = context.getBean(DataSetGetMetadata.class, preparation.getDataSetId()).execute();
        return getPreparationExportTypesForDataSet(metadata.getId());
    }

    @Override
    public Stream<ExportFormatMessage> getPreparationExportTypesForDataSet(String dataSetId) {
        final DataSetMetadata metadata = context.getBean(DataSetGetMetadata.class, dataSetId).execute();

        return formatRegistrationService.getExternalFormats() //
                .sorted(Comparator.comparingInt(ExportFormat::getOrder)) // Enforce strict order.
                .filter(ExportFormat::isEnabled) //
                .filter(f -> f.isCompatible(metadata)) //
                .map(f -> beanConversionService.convert(f, ExportFormatMessage.class));
    }

    @Override
    public StreamingResponseBody getDictionary() {
        return outputStream -> {
            final File dictionaryPath = new File(analyzerService.getIndexesLocation() + "/index/dictionary/default/");
            LOG.debug("Returning dictionary at path '{}'", dictionaryPath.getAbsoluteFile());
            final File keywordPath = new File(analyzerService.getIndexesLocation() + "/index/keyword/default/");
            LOG.debug("Returning keywords at path '{}'", keywordPath.getAbsoluteFile());

            // Read lucene directory and build BroadcastIndexObject instance
            final BroadcastIndexObject dictionary, keyword;
            try (FSDirectory directory = FSDirectory.open(dictionaryPath)) {
                dictionary = new BroadcastIndexObject(directory);
            }
            try (FSDirectory directory = FSDirectory.open(keywordPath)) {
                keyword = new BroadcastIndexObject(directory);
            }

            // Serialize it to output
            LOG.debug("Returning dictionaries '{}' / '{}'", dictionary, keyword);
            Dictionaries result = new Dictionaries(dictionary, keyword);
            try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(outputStream))) {
                oos.writeObject(result);
            }
        };
    }

    @Override
    public List<SemanticDomain> getPreparationColumnSemanticCategories(String preparationId, String columnId, String stepId) {

        LOG.debug("listing preparation semantic categories for preparation #{} column #{}@{}", preparationId, columnId, stepId);

        // get the preparation
        final Preparation preparation = getPreparation(preparationId);

        // get the step (in case of 'head', the real step id must be found)
        final String version = StringUtils.equals("head", stepId) ? //
                preparation.getSteps().get(preparation.getSteps().size() - 1).getId() : stepId;

        /*
         * OK, this one is a bit tricky so pay attention.
         *
         * To be able to get the semantic types, the analyzer service needs to run on the result of the preparation.
         *
         * The result must be found in the cache, so if the preparation is not cached, the preparation is run so that
         * it gets cached.
         *
         * Then, the analyzer service just gets the data from the cache. That's it.
         */

        // generate the cache keys for both metadata & content
        final ContentCacheKey metadataKey = cacheKeyGenerator.metadataBuilder() //
                .preparationId(preparationId).stepId(version).sourceType(HEAD).build();

        final ContentCacheKey contentKey = cacheKeyGenerator.contentBuilder() //
                .datasetId(preparation.getDataSetId()).preparationId(preparationId).stepId(version) //
                .format(JSON).sourceType(HEAD) //
                .build();

        // if the preparation is not cached, let's compute it to have some cache
        if (!contentCache.has(metadataKey) || !contentCache.has(contentKey)) {
            addPreparationInCache(preparation, stepId);
        }

        // run the analyzer service on the cached content
        try {
            final DataSetMetadata metadata = mapper.readerFor(DataSetMetadata.class).readValue(contentCache.get(metadataKey));
            final List<SemanticDomain> semanticDomains = getSemanticDomains(metadata, columnId, contentCache.get(contentKey));
            LOG.debug("found {} for preparation #{}, column #{}", semanticDomains, preparationId, columnId);
            return semanticDomains;

        } catch (IOException e) {
            throw new TDPException(UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * Get the preparation from the preparation service.
     *
     * @param preparationId the wanted preparation id.
     * @return the preparation from the preparation service.
     */
    private Preparation getPreparation(String preparationId) {
        final Preparation preparation;
        try {
            final PreparationDetailsGet details = applicationContext.getBean(PreparationDetailsGet.class, preparationId);
            preparation = mapper.readerFor(Preparation.class).readValue(details.execute());
        } catch (IOException e) {
            throw new TDPException(PREPARATION_DOES_NOT_EXIST, e, build().put("id", preparationId));
        }
        return preparation;
    }

    /**
     * Return the semantic domains for the given parameters.
     *
     * @param metadata the dataset metadata.
     * @param columnId the column id to analyze.
     * @param records the dataset records.
     * @return the semantic domains for the given parameters.
     * @throws IOException can happen...
     */
    private List<SemanticDomain> getSemanticDomains(DataSetMetadata metadata, String columnId, InputStream records)
            throws IOException {

        // copy the column metadata and set the semantic domain forced flag to false to make sure the statistics adapter set all
        // available domains
        final ColumnMetadata columnMetadata = column() //
                .copy(metadata.getRowMetadata().getById(columnId)) //
                .semanticDomainForce(false) //
                .build();

        final Analyzer<Analyzers.Result> analyzer = analyzerService.build(columnMetadata, SEMANTIC);
        analyzer.init();

        try (final JsonParser parser = mapper.getFactory().createParser(records)) {
            final DataSet dataSet = mapper.readerFor(DataSet.class).readValue(parser);
            dataSet.getRecords() //
                    .map(r -> r.get(columnId)) //
                    .forEach(analyzer::analyze);
            analyzer.end();
        }

        final List<Analyzers.Result> analyzerResult = analyzer.getResult();
        statisticsAdapter.adapt(singletonList(columnMetadata), analyzerResult);

        return columnMetadata.getSemanticDomains();
    }

    /**
     * Add the following preparation in cache.
     *
     * @param preparation the preparation to cache.
     * @param stepId the preparation step id.
     */
    private void addPreparationInCache(Preparation preparation, String stepId) {
        final ExportParameters exportParameters = new ExportParameters();
        exportParameters.setPreparationId(preparation.getId());
        exportParameters.setExportType("JSON");
        exportParameters.setStepId(stepId);
        exportParameters.setDatasetId(preparation.getDataSetId());

        final StreamingResponseBody streamingResponseBody = executeSampleExportStrategy(exportParameters);
        try {
            // the result is not important here as it will be cached !
            streamingResponseBody.writeTo(new NullOutputStream());
        } catch (IOException e) {
            throw new TDPException(UNEXPECTED_EXCEPTION, e);
        }
    }

}
