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

import static org.talend.dataprep.command.CommandHelper.toStreaming;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Call;
import org.talend.daikon.annotation.Client;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.Import;
import org.talend.dataprep.api.dataset.statistics.SemanticDomain;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.api.EnrichedDataSetMetadata;
import org.talend.dataprep.api.service.command.dataset.*;
import org.talend.dataprep.api.service.command.preparation.PreparationSearchByDataSetId;
import org.talend.dataprep.api.service.command.transformation.SuggestDataSetActions;
import org.talend.dataprep.api.service.command.transformation.SuggestLookupActions;
import org.talend.dataprep.command.CommandHelper;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.command.dataset.DataSetGetMetadata;
import org.talend.dataprep.dataset.service.UserDataSetMetadata;
import org.talend.dataprep.dataset.service.api.UpdateColumnParameters;
import org.talend.dataprep.preparation.service.UserPreparation;
import org.talend.dataprep.util.SortAndOrderHelper;
import org.talend.dataprep.util.SortAndOrderHelper.Order;
import org.talend.dataprep.util.SortAndOrderHelper.Sort;
import org.talend.services.dataprep.DataSetService;
import org.talend.services.dataprep.PreparationService;
import org.talend.services.dataprep.api.DataSetAPI;

import com.netflix.hystrix.HystrixCommand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ServiceImplementation
public class DataSetAPIImpl extends APIService implements DataSetAPI {

    @Client
    DataSetService dataSetService;

    @Client
    PreparationService preparationService;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        // This allow to bind Sort and Order parameters in lower-case even if the key is uppercase.
        // URLs are cleaner in lowercase.
        binder.registerCustomEditor(Sort.class, SortAndOrderHelper.getSortPropertyEditor());
        binder.registerCustomEditor(Order.class, SortAndOrderHelper.getOrderPropertyEditor());
    }

    @Override
    @Call(using = CreateDataSet.class)
    public native String create(String name, String tag, String contentType, InputStream dataSetContent);

    @Override
    @Call(using = CreateOrUpdateDataSet.class)
    public native String createOrUpdateById(String name, String id, InputStream dataSetContent);

    @Override
    public Callable<String> copy(String name, String id) {
        return () -> dataSetService.copy(id, name);
    }

    @Override
    @Call(service = DataSetService.class, operation = "updateDataSet")
    public native void updateMetadata(String id, DataSetMetadata dataSetContent);

    @Override
    @Call(using = UpdateDataSet.class)
    public native String update(String id, InputStream dataSetContent);

    @Override
    @Call(service = DataSetService.class, operation = "updateDatasetColumn")
    public native void updateColumn(String datasetId, String columnId, UpdateColumnParameters parameters);

    @Override
    public DataSet get(String id, boolean fullContent, boolean includeTechnicalProperties) {
        return dataSetService.get(true, includeTechnicalProperties, id);
    }

    @Override
    public DataSetMetadata getMetadata(String id) {
        return dataSetService.getMetadata(id);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> preview(String id, boolean metadata, String sheetName) {
        dataSetService.preview(metadata, sheetName, id);
        return new ResponseEntity<>(HttpStatus.MULTI_STATUS);
    }

    @Override
    public Callable<Stream<UserDataSetMetadata>> list(Sort sort, Order order, String name, boolean certified, boolean favorite,
            boolean limit) {
        return dataSetService.list(sort, order, name, certified, favorite, limit);
    }

    @Override
    public Callable<Stream<EnrichedDataSetMetadata>> listSummary(Sort sort, Order order, String name, boolean certified,
            boolean favorite, boolean limit) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing datasets summary (pool: {})...", getConnectionStats());
        }
        return () -> {
            GenericCommand<InputStream> listDataSets = getCommand(DataSetList.class, sort, order, name, certified, favorite,
                    limit);
            return Flux.from(CommandHelper.toPublisher(UserDataSetMetadata.class, mapper, listDataSets)) //
                    .map(m -> {
                        // Add the related preparations list to the given dataset metadata.
                        final PreparationSearchByDataSetId getPreparations = getCommand(PreparationSearchByDataSetId.class,
                                m.getId());
                        return Flux.from(CommandHelper.toPublisher(Preparation.class, mapper, getPreparations)).collectList() //
                                .map(preparations -> {
                                    final List<Preparation> list = preparations.stream() //
                                            .filter(p -> p.getSteps() != null) //
                                            .collect(Collectors.toList());
                                    return new EnrichedDataSetMetadata(m, list);
                                }) //
                                .block();
                    }) //
                    .toStream(1);
        };
    }

    @Override
    public Callable<Stream<UserDataSetMetadata>> listCompatibleDatasets(String id, Sort sort, Order order) {
        return () -> StreamSupport.stream(dataSetService.listCompatibleDatasets(id, sort, order).spliterator(), false);
    }

    @Override
    public Callable<Stream<UserPreparation>> listCompatiblePreparations(String dataSetId, Sort sort, Order order) {
        return () -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Listing compatible preparations (pool: {})...", getConnectionStats());
            }
            // get the list of compatible data sets
            final Mono<List<UserDataSetMetadata>> compatibleList = Flux
                    .fromIterable(dataSetService.listCompatibleDatasets(dataSetId, sort, order)) //
                    .collectList() //
                    .cache(); // Keep it in cache for later reuse
            // get list of preparations
            return Flux.fromStream( //
                    preparationService.listAll(sort, order)) //
                    .filter(p -> compatibleList.flatMapIterable(l -> l) //
                    .map(DataSetMetadata::getId) //
                    .any(id -> StringUtils.equals(id, p.getDataSetId()) || dataSetId.equals(p.getDataSetId())) //
                    .block() //
            ) //
            .toStream(1);
        };
    }

    @Override
    public void delete(String id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Delete dataset #{} (pool: {})...", id, getConnectionStats());
        }
        HystrixCommand<Void> deleteCommand = getCommand(DataSetDelete.class, id);

        deleteCommand.execute();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing datasets (pool: {}) done.", getConnectionStats());
        }
    }

    @Override
    public void processCertification(String id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ask certification for dataset #{}", id);
        }
        HystrixCommand<Void> command = getCommand(DatasetCertification.class, id);
        command.execute();
    }

    @Override
    public StreamingResponseBody suggestDatasetActions(String id) {
        // Get dataset metadata
        HystrixCommand<DataSetMetadata> retrieveMetadata = getCommand(DataSetGetMetadata.class, id);
        // Asks transformation service for suggested actions for column type and domain...
        HystrixCommand<String> getSuggestedActions = getCommand(SuggestDataSetActions.class, retrieveMetadata);
        // ... also adds lookup actions
        HystrixCommand<InputStream> getLookupActions = getCommand(SuggestLookupActions.class, getSuggestedActions, id);
        // Returns actions
        return toStreaming(getLookupActions);
    }

    @Override
    public Callable<String> favorite(String id, boolean unset) {
        return () -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug((unset ? "Unset" : "Set") + " favorite dataset #{} (pool: {})...", id, getConnectionStats());
            }
            HystrixCommand<String> creation = getCommand(SetFavorite.class, id, unset);
            String result = creation.execute();
            LOG.debug("Set Favorite for user (can'tget user now) #{} done.", id);
            return result;
        };
    }

    @Override
    public Callable<Stream<String>> listEncodings() {
        return () -> CommandHelper.toStream(String.class, mapper, getCommand(DataSetGetEncodings.class));
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getImportParameters(String importType) {
        return toStreaming(getCommand(DataSetGetImportParameters.class, importType));
    }

    @Override
    public Callable<Stream<Import>> listImports() {
        return () -> CommandHelper.toStream(Import.class, mapper, getCommand(DataSetGetImports.class));
    }

    @Override
    public Callable<Stream<SemanticDomain>> getDataSetColumnSemanticCategories(String datasetId, String columnId) {
        return () -> {
            LOG.debug("listing semantic types for dataset {}, column {}", datasetId, columnId);
            return CommandHelper.toStream(SemanticDomain.class, mapper,
                    getCommand(GetDataSetColumnTypes.class, datasetId, columnId));
        };
    }
}
