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

package org.talend.dataprep.dataset;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.talend.daikon.exception.ExceptionContext.build;
import static org.talend.dataprep.exception.error.DataSetErrorCodes.UNABLE_TO_CREATE_OR_UPDATE_DATASET;
import static org.talend.dataprep.quality.AnalyzerService.Analysis.SEMANTIC;
import static org.talend.dataprep.util.SortAndOrderHelper.getDataSetMetadataComparator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.dataset.*;
import org.talend.dataprep.api.dataset.DataSetGovernance.Certification;
import org.talend.dataprep.api.dataset.Import.ImportBuilder;
import org.talend.dataprep.api.dataset.location.DataSetLocationService;
import org.talend.dataprep.api.dataset.location.LocalStoreLocation;
import org.talend.dataprep.api.dataset.location.locator.DataSetLocatorService;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.api.dataset.row.FlagNames;
import org.talend.dataprep.api.dataset.statistics.SemanticDomain;
import org.talend.dataprep.api.service.info.VersionService;
import org.talend.dataprep.api.user.UserData;
import org.talend.dataprep.configuration.EncodingSupport;
import org.talend.dataprep.conversions.BeanConversionService;
import org.talend.dataprep.dataset.event.DataSetMetadataBeforeUpdateEvent;
import org.talend.dataprep.dataset.event.DataSetRawContentUpdateEvent;
import org.talend.dataprep.dataset.service.UserDataSetMetadata;
import org.talend.dataprep.dataset.service.analysis.synchronous.ContentAnalysis;
import org.talend.dataprep.dataset.service.analysis.synchronous.FormatAnalysis;
import org.talend.dataprep.dataset.service.analysis.synchronous.SchemaAnalysis;
import org.talend.dataprep.dataset.service.api.UpdateColumnParameters;
import org.talend.dataprep.dataset.store.content.StrictlyBoundedInputStream;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.DataSetErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.grants.AccessGrantChecker;
import org.talend.dataprep.grants.CommonRestrictedActions;
import org.talend.dataprep.http.HttpResponseContext;
import org.talend.dataprep.lock.DistributedLock;
import org.talend.dataprep.log.Markers;
import org.talend.dataprep.parameters.jsonschema.ComponentProperties;
import org.talend.dataprep.quality.AnalyzerService;
import org.talend.dataprep.schema.DraftValidator;
import org.talend.dataprep.schema.FormatFamily;
import org.talend.dataprep.schema.FormatFamilyFactory;
import org.talend.dataprep.schema.Schema;
import org.talend.dataprep.security.Security;
import org.talend.dataprep.user.store.UserDataRepository;
import org.talend.dataprep.util.SortAndOrderHelper;
import org.talend.dataprep.util.SortAndOrderHelper.Order;
import org.talend.dataprep.util.SortAndOrderHelper.Sort;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.services.dataprep.DataSetService;

@ServiceImplementation
public class DataSetServiceImpl extends BaseDataSetService implements DataSetService {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(DataSetServiceImpl.class);

    /** Date format to use. */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-YYYY HH:mm"); // $NON-NLS-1

    private static final String CONTENT_TYPE = "Content-Type";

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Format analyzer needed to update the schema.
     */
    @Autowired
    private FormatAnalysis formatAnalyzer;

    /**
     * User repository.
     */
    @Autowired
    private UserDataRepository userDataRepository;

    /**
     * Format guess factory.
     */
    @Autowired
    private FormatFamilyFactory formatFamilyFactory;

    /**
     * Dataset locator (used for remote datasets).
     */
    @Autowired
    private DataSetLocatorService datasetLocator;

    /**
     * DataPrep abstraction to the underlying security (whether it's enabled or not).
     */
    @Autowired
    private Security security;

    /**
     * Encoding support service.
     */
    @Autowired
    private EncodingSupport encodings;

    /**
     * All possible data set locations.
     */
    @Autowired
    private DataSetLocationService locationsService;

    @Autowired
    private VersionService versionService;

    @Autowired
    private AccessGrantChecker accessGrantChecker;

    @Autowired
    private BeanConversionService conversionService;

    @Value("#{'${dataset.imports}'.split(',')}")
    private Set<String> enabledImports;

    @Value("${dataset.list.limit:10}")
    private int datasetListLimit;

    @Autowired
    private AnalyzerService analyzerService;

    @Value("${dataset.local.file.size.limit:2000000}")
    private long maximumInputStreamSize;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        // This allow to bind Sort and Order parameters in lower-case even if the key is uppercase.
        // URLs are cleaner in lowercase.
        binder.registerCustomEditor(Sort.class, SortAndOrderHelper.getSortPropertyEditor());
        binder.registerCustomEditor(Order.class, SortAndOrderHelper.getOrderPropertyEditor());
    }

    @Override
    public Callable<Stream<UserDataSetMetadata>> list(Sort sort, Order order, String name, boolean certified, boolean favorite,
            boolean limit) {
        return () -> {
            // Build filter for data sets
            String userId = security.getUserId();
            final UserData userData = userDataRepository.get(userId);
            final List<String> predicates = new ArrayList<>();
            predicates.add("lifecycle.importing = false");
            if (favorite) {
                if (userData != null && !userData.getFavoritesDatasets().isEmpty()) {
                    predicates.add("id in [" + userData.getFavoritesDatasets().stream().map(ds -> '\'' + ds + '\'')
                            .collect(Collectors.joining(",")) + "]");
                } else {
                    // Wants favorites but user has no favorite
                    return Stream.empty();
                }
            }
            if (certified) {
                predicates.add("governance.certificationStep = '" + Certification.CERTIFIED + "'");
            }
            if (!StringUtils.isEmpty(name)) {
                predicates.add("name contains '" + name + "'");
            }
            final String tqlFilter = predicates.stream().collect(Collectors.joining(" and "));
            LOG.debug("TQL Filter in use: {}", tqlFilter);

            // Get all data sets according to filter
            try (Stream<DataSetMetadata> stream = dataSetMetadataRepository.list(tqlFilter, sort, order)) {
                Stream<UserDataSetMetadata> userDataSetMetadataStream = stream
                        .map(m -> conversionService.convert(m, UserDataSetMetadata.class));
                if (sort == Sort.AUTHOR || sort == Sort.NAME) { // As theses are not well handled by mongo repository
                    userDataSetMetadataStream = userDataSetMetadataStream.sorted(getDataSetMetadataComparator(sort, order));
                }
                return userDataSetMetadataStream.limit(limit ? datasetListLimit : Long.MAX_VALUE);
            }
        };
    }

    @Override
    public Iterable<UserDataSetMetadata> listCompatibleDatasets(String id, Sort sort, Order order) {

        Spliterator<DataSetMetadata> iterator = dataSetMetadataRepository.listCompatible(id).spliterator();

        final Comparator<DataSetMetadata> comparator = getDataSetMetadataComparator(sort, order);

        // Return sorted results
        try (Stream<DataSetMetadata> stream = stream(iterator, false)) {
            return stream.filter(metadata -> !metadata.getLifecycle().isImporting()) //
                    .map(m -> conversionService.convert(m, UserDataSetMetadata.class)) //
                    .sorted(comparator) //
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String create(String name, String tag, String contentType, InputStream content) throws IOException {
        HttpResponseContext.header(CONTENT_TYPE, TEXT_PLAIN_VALUE);

        final String id = UUID.randomUUID().toString();
        final Marker marker = Markers.dataset(id);
        LOG.debug(marker, "Creating...");

        // check that the name is not already taken
        checkIfNameIsAvailable(name);

        // get the location out of the content type and the request body
        final DataSetLocation location;
        try {
            location = datasetLocator.getDataSetLocation(contentType, content);
        } catch (IOException e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_READ_DATASET_LOCATION, e);
        }
        DataSetMetadata dataSetMetadata = null;
        final TDPException hypotheticalException;
        try {
            dataSetMetadata = metadataBuilder.metadata() //
                    .id(id) //
                    .name(name) //
                    .author(security.getUserId()) //
                    .location(location) //
                    .created(System.currentTimeMillis()) //
                    .tag(tag) //
                    .build();

            dataSetMetadata.getLifecycle().setImporting(true); // Indicate data set is being imported

            // Save data set content
            LOG.debug(marker, "Storing content...");
            contentStore.storeAsRaw(dataSetMetadata, new StrictlyBoundedInputStream(content, maximumInputStreamSize));
            LOG.debug(marker, "Content stored.");

            // Create the new data set
            dataSetMetadataRepository.save(dataSetMetadata);
            LOG.debug(marker, "dataset metadata stored {}", dataSetMetadata);

            // Queue events (format analysis, content indexing for search...)
            analyzeDataSet(id, true, emptyList());

            LOG.debug(marker, "Created!");
            return id;
        } catch (StrictlyBoundedInputStream.InputStreamTooLargeException e) {
            hypotheticalException = new TDPException(DataSetErrorCodes.LOCAL_DATA_SET_INPUT_STREAM_TOO_LARGE, e,
                    build().put("limit", maximumInputStreamSize));
        } catch (TDPException e) {
            hypotheticalException = e;
        } catch (Exception e) {
            hypotheticalException = new TDPException(DataSetErrorCodes.UNABLE_CREATE_DATASET, e);
        }
        dataSetMetadataRepository.remove(id);
        if (dataSetMetadata != null) {
            contentStore.delete(dataSetMetadata);
        }
        throw hypotheticalException;
    }

    @Override
    public DataSet get(boolean metadata, boolean includeInternalContent, String id) {
        final Marker marker = Markers.dataset(id);
        LOG.debug(marker, "Get data set #{}", id);
        try {
            DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(id);
            assertDataSetMetadata(dataSetMetadata, id);
            // Build the result
            DataSet dataSet = new DataSet();
            if (metadata) {
                dataSet.setMetadata(conversionService.convert(dataSetMetadata, UserDataSetMetadata.class));
            }
            Stream<DataSetRow> stream = contentStore.stream(dataSetMetadata, -1); // Disable line limit
            if (!includeInternalContent) {
                LOG.debug("Skip internal content when serving data set #{} content.", id);
                stream = stream.map(r -> {
                    final Map<String, Object> values = r.values();
                    final Map<String, Object> filteredValues = new HashMap<>(values);
                    values.forEach((k, v) -> {
                        if (k != null && k.startsWith(FlagNames.INTERNAL_PROPERTY_PREFIX)) { // Removes technical properties
                                                                                             // from returned values.
                            filteredValues.remove(k);
                        }
                    });
                    filteredValues.put(FlagNames.TDP_ID, r.getTdpId()); // Include TDP_ID anyway
                    return new DataSetRow(r.getRowMetadata(), filteredValues);
                });
            }
            dataSet.setRecords(stream);
            return dataSet;
        } finally {
            LOG.debug(marker, "Get done.");
        }
    }

    @ResponseBody
    @Override
    public DataSetMetadata getMetadata(String id) {
        if (id == null) {
            HttpResponseContext.status(HttpStatus.NO_CONTENT);
            return null;
        }

        LOG.debug("get dataset metadata for {}", id);

        DataSetMetadata metadata = dataSetMetadataRepository.get(id);
        if (metadata == null) {
            throw new TDPException(DataSetErrorCodes.DATASET_DOES_NOT_EXIST, build().put("id", id));
        }
        if (!metadata.getLifecycle().isSchemaAnalyzed()) {
            HttpResponseContext.status(HttpStatus.ACCEPTED);
            return new DataSetMetadata();
        }
        LOG.info("found dataset {} for #{}", metadata.getName(), id);
        return metadata;
    }

    @Override
    public void delete(String id) {
        DataSetMetadata metadata = dataSetMetadataRepository.get(id);
        final DistributedLock lock = dataSetMetadataRepository.createDatasetMetadataLock(id);
        try {
            lock.lock();
            if (metadata != null) {
                dataSetMetadataRepository.remove(id); // first remove the metadata as there may be additional check
                contentStore.delete(metadata);
            } // do nothing if the dataset does not exists
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String copy(String dataSetId, String copyName) {

        HttpResponseContext.header(CONTENT_TYPE, TEXT_PLAIN_VALUE);

        DataSetMetadata original = dataSetMetadataRepository.get(dataSetId);
        if (original == null) {
            return StringUtils.EMPTY;
        }

        // use a default name if empty (original name + " Copy" )
        final String newName;
        if (StringUtils.isBlank(copyName)) {
            newName = original.getName() + " Copy";
        } else {
            newName = copyName;
        }

        final DistributedLock lock = dataSetMetadataRepository.createDatasetMetadataLock(dataSetId);
        try {
            lock.lock(); // lock to ensure any asynchronous analysis is completed.

            // check that the name is not already taken
            checkIfNameIsAvailable(newName);

            // Create copy (based on original data set metadata)
            final String newId = UUID.randomUUID().toString();
            final Marker marker = Markers.dataset(newId);
            LOG.debug(marker, "Cloning...");
            DataSetMetadata target = metadataBuilder.metadata() //
                    .copy(original) //
                    .id(newId) //
                    .name(newName) //
                    .author(security.getUserId()) //
                    .location(original.getLocation()) //
                    .created(System.currentTimeMillis()) //
                    .build();

            // Save data set content
            LOG.debug(marker, "Storing content...");
            try (InputStream content = contentStore.getAsRaw(original)) {
                contentStore.storeAsRaw(target, content);
            } catch (IOException e) {
                throw new TDPException(DataSetErrorCodes.UNABLE_CREATE_DATASET, e);
            }

            LOG.debug(marker, "Content stored.");

            // Create the new data set
            dataSetMetadataRepository.save(target);

            LOG.info(marker, "Copy done --> {}", newId);

            return newId;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void processCertification(String id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ask certification for dataset #{}", id);
        }

        // Check if the user has sufficient grants to perform the action
        accessGrantChecker.allowed(CommonRestrictedActions.CERTIFICATION);

        DistributedLock datasetLock = dataSetMetadataRepository.createDatasetMetadataLock(id);
        datasetLock.lock();
        try {
            DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(id);
            if (dataSetMetadata != null) {
                LOG.trace("Current certification step is " + dataSetMetadata.getGovernance().getCertificationStep());

                if (dataSetMetadata.getGovernance().getCertificationStep() == Certification.NONE) {
                    dataSetMetadata.getGovernance().setCertificationStep(Certification.PENDING);
                    dataSetMetadataRepository.save(dataSetMetadata);
                } else if (dataSetMetadata.getGovernance().getCertificationStep() == Certification.PENDING) {
                    dataSetMetadata.getGovernance().setCertificationStep(Certification.CERTIFIED);
                    dataSetMetadataRepository.save(dataSetMetadata);
                } else if (dataSetMetadata.getGovernance().getCertificationStep() == Certification.CERTIFIED) {
                    dataSetMetadata.getGovernance().setCertificationStep(Certification.NONE);
                    dataSetMetadataRepository.save(dataSetMetadata);
                }

                LOG.debug("New certification step is " + dataSetMetadata.getGovernance().getCertificationStep());
            } // else do nothing if the dataset does not exists
        } finally {
            datasetLock.unlock();
        }
    }

    @Override
    public void updateRawDataSet(String id, String name, InputStream dataSetContent) {

        LOG.debug("updating dataset content #{}", id);

        final DistributedLock lock = dataSetMetadataRepository.createDatasetMetadataLock(id);
        try {
            lock.lock();
            final DataSetMetadataBuilder datasetBuilder = metadataBuilder.metadata().id(id);
            final DataSetMetadata metadataForUpdate = dataSetMetadataRepository.get(id);
            if (metadataForUpdate != null) {
                datasetBuilder.copyNonContentRelated(metadataForUpdate);
                datasetBuilder.modified(System.currentTimeMillis());
            }
            if (!StringUtils.isEmpty(name)) {
                datasetBuilder.name(name);
            }
            final DataSetMetadata dataSetMetadata = datasetBuilder.build();

            // Save data set content
            contentStore.storeAsRaw(dataSetMetadata, dataSetContent);
            dataSetMetadataRepository.save(dataSetMetadata);
            publisher.publishEvent(new DataSetRawContentUpdateEvent(dataSetMetadata));
        } finally {
            lock.unlock();
        }
        // Content was changed, so queue events (format analysis, content indexing for search...)
        analyzeDataSet(id, true, emptyList());
    }

    @Override
    public Iterable<JsonErrorCodeDescription> listErrors() {
        // need to cast the typed dataset errors into mock ones to use json parsing
        List<JsonErrorCodeDescription> errors = new ArrayList<>(DataSetErrorCodes.values().length);
        for (DataSetErrorCodes code : DataSetErrorCodes.values()) {
            errors.add(new JsonErrorCodeDescription(code));
        }
        return errors;
    }

    @Override
    public DataSet preview(boolean metadata, String sheetName, String id) {

        DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(id);

        if (dataSetMetadata == null) {
            HttpResponseContext.status(HttpStatus.NO_CONTENT);
            return DataSet.empty(); // No data set, returns empty content.
        }
        if (!dataSetMetadata.isDraft()) {
            // Moved to get data set content operation
            HttpResponseContext.status(HttpStatus.MOVED_PERMANENTLY);
            HttpResponseContext.header("Location", "/datasets/" + id + "/content");
            return DataSet.empty(); // dataset not anymore a draft so preview doesn't make sense.
        }
        if (StringUtils.isNotEmpty(sheetName)) {
            dataSetMetadata.setSheetName(sheetName);
        }
        // take care of previous data without schema parser result
        if (dataSetMetadata.getSchemaParserResult() != null) {
            // sheet not yet set correctly so use the first one
            if (StringUtils.isEmpty(dataSetMetadata.getSheetName())) {
                String theSheetName = dataSetMetadata.getSchemaParserResult().getSheetContents().get(0).getName();
                LOG.debug("preview for dataSetMetadata: {} with sheetName: {}", id, theSheetName);
                dataSetMetadata.setSheetName(theSheetName);
            }

            String theSheetName = dataSetMetadata.getSheetName();

            Optional<Schema.SheetContent> sheetContentFound = dataSetMetadata.getSchemaParserResult().getSheetContents().stream()
                    .filter(sheetContent -> theSheetName.equals(sheetContent.getName())).findFirst();

            if (!sheetContentFound.isPresent()) {
                HttpResponseContext.status(HttpStatus.NO_CONTENT);
                return DataSet.empty(); // No sheet found, returns empty content.
            }

            List<ColumnMetadata> columnMetadatas = sheetContentFound.get().getColumnMetadatas();

            if (dataSetMetadata.getRowMetadata() == null) {
                dataSetMetadata.setRowMetadata(new RowMetadata(emptyList()));
            }

            dataSetMetadata.getRowMetadata().setColumns(columnMetadatas);
        } else {
            LOG.warn("dataset#{} has draft status but any SchemaParserResult");
        }
        // Build the result
        DataSet dataSet = new DataSet();
        if (metadata) {
            dataSet.setMetadata(conversionService.convert(dataSetMetadata, UserDataSetMetadata.class));
        }
        dataSet.setRecords(contentStore.stream(dataSetMetadata).limit(100));
        return dataSet;
    }

    @Override
    public void updateDataSet(String id, DataSetMetadata dataSetMetadata) {
        final DistributedLock lock = dataSetMetadataRepository.createDatasetMetadataLock(id);
        lock.lock();
        try {
            LOG.debug("updateDataSet: {}", dataSetMetadata);
            publisher.publishEvent(new DataSetMetadataBeforeUpdateEvent(dataSetMetadata));

            //
            // Only part of the metadata can be updated, so the original dataset metadata is loaded and updated
            //
            DataSetMetadata metadataForUpdate = dataSetMetadataRepository.get(id);
            DataSetMetadata original = metadataBuilder.metadata().copy(metadataForUpdate).build();

            if (metadataForUpdate == null) {
                // No need to silently create the data set metadata: associated content will most likely not exist.
                throw new TDPException(DataSetErrorCodes.DATASET_DOES_NOT_EXIST, build().put("id", id));
            }

            try {
                // update the name
                metadataForUpdate.setName(dataSetMetadata.getName());

                // update the sheet content (in case of a multi-sheet excel file)
                if (metadataForUpdate.getSchemaParserResult() != null) {
                    Optional<Schema.SheetContent> sheetContentFound = metadataForUpdate.getSchemaParserResult().getSheetContents()
                            .stream().filter(sheetContent -> dataSetMetadata.getSheetName().equals(sheetContent.getName()))
                            .findFirst();

                    if (sheetContentFound.isPresent()) {
                        List<ColumnMetadata> columnMetadatas = sheetContentFound.get().getColumnMetadatas();
                        if (metadataForUpdate.getRowMetadata() == null) {
                            metadataForUpdate.setRowMetadata(new RowMetadata(emptyList()));
                        }
                        metadataForUpdate.getRowMetadata().setColumns(columnMetadatas);
                    }

                    metadataForUpdate.setSheetName(dataSetMetadata.getSheetName());
                    metadataForUpdate.setSchemaParserResult(null);
                }

                // Location updates
                metadataForUpdate.setLocation(dataSetMetadata.getLocation());

                // update parameters & encoding (so that user can change import parameters for CSV)
                metadataForUpdate.getContent().setParameters(dataSetMetadata.getContent().getParameters());
                metadataForUpdate.setEncoding(dataSetMetadata.getEncoding());

                // update limit
                final Optional<Long> newLimit = dataSetMetadata.getContent().getLimit();
                if (newLimit.isPresent()) {
                    metadataForUpdate.getContent().setLimit(newLimit.get());
                }

                // Validate that the new data set metadata and removes the draft status
                final String formatFamilyId = dataSetMetadata.getContent().getFormatFamilyId();
                if (formatFamilyFactory.hasFormatFamily(formatFamilyId)) {
                    FormatFamily format = formatFamilyFactory.getFormatFamily(formatFamilyId);
                    try {
                        DraftValidator draftValidator = format.getDraftValidator();
                        DraftValidator.Result result = draftValidator.validate(dataSetMetadata);
                        if (result.isDraft()) {
                            // This is not an exception case: data set may remain a draft after update (although rather
                            // unusual)
                            LOG.warn("Data set #{} is still a draft after update.", id);
                            return;
                        }
                        // Data set metadata to update is no longer a draft
                        metadataForUpdate.setDraft(false);
                    } catch (UnsupportedOperationException e) {
                        // no need to validate draft here
                    }
                }

                // update schema
                formatAnalyzer.update(original, metadataForUpdate);

                // save the result
                dataSetMetadataRepository.save(metadataForUpdate);

                // all good mate!! so send that to jms
                // Asks for a in depth schema analysis (for column type information).
                analyzeDataSet(id, true, singletonList(FormatAnalysis.class));
            } catch (TDPException e) {
                throw e;
            } catch (Exception e) {
                throw new TDPException(UNABLE_TO_CREATE_OR_UPDATE_DATASET, e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterable<String> favorites() {
        String userId = security.getUserId();
        UserData userData = userDataRepository.get(userId);
        return userData != null ? userData.getFavoritesDatasets() : emptyList();
    }

    @Override
    public void setFavorites(boolean unset, String id) {
        String userId = security.getUserId();
        // check that dataset exists
        DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(id);
        if (dataSetMetadata != null) {
            LOG.debug("{} favorite dataset for #{} for user {}", unset ? "Unset" : "Set", id, userId); // $NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

            UserData userData = userDataRepository.get(userId);
            if (unset) {// unset the favorites
                if (userData != null) {
                    userData.getFavoritesDatasets().remove(id);
                    userDataRepository.save(userData);
                } // no user data for this user so nothing to unset
            } else {// set the favorites
                if (userData == null) {// let's create a new UserData
                    userData = new UserData(userId, versionService.version().getVersionId());
                } // else already created so just update it.
                userData.addFavoriteDataset(id);
                userDataRepository.save(userData);
            }
        } else {// no dataset found so throws an error
            throw new TDPException(DataSetErrorCodes.DATASET_DOES_NOT_EXIST, build().put("id", id));
        }
    }

    @Override
    public void updateDatasetColumn(String dataSetId, String columnId, UpdateColumnParameters parameters) {

        final DistributedLock lock = dataSetMetadataRepository.createDatasetMetadataLock(dataSetId);
        lock.lock();
        try {

            // check that dataset exists
            final DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(dataSetId);
            if (dataSetMetadata == null) {
                throw new TDPException(DataSetErrorCodes.DATASET_DOES_NOT_EXIST, build().put("id", dataSetId));
            }

            LOG.debug("update dataset column for #{} with type {} and/or domain {}", dataSetId, parameters.getType(),
                    parameters.getDomain());

            // get the column
            final ColumnMetadata column = dataSetMetadata.getRowMetadata().getById(columnId);
            if (column == null) {
                throw new TDPException(DataSetErrorCodes.COLUMN_DOES_NOT_EXIST, //
                        build() //
                                .put("id", dataSetId) //
                                .put("columnid", columnId));
            }

            // update type/domain
            if (parameters.getType() != null) {
                column.setType(parameters.getType());
            }
            if (parameters.getDomain() != null) {
                // erase domain to let only type
                if (parameters.getDomain().isEmpty()) {
                    column.setDomain("");
                    column.setDomainLabel("");
                    column.setDomainFrequency(0);
                }
                // change domain
                else {
                    final SemanticDomain semanticDomain = column.getSemanticDomains() //
                            .stream() //
                            .filter(dom -> StringUtils.equals(dom.getId(), parameters.getDomain())) //
                            .findFirst().orElse(null);
                    if (semanticDomain != null) {
                        column.setDomain(semanticDomain.getId());
                        column.setDomainLabel(semanticDomain.getLabel());
                        column.setDomainFrequency(semanticDomain.getFrequency());
                    }
                }
            }

            // save
            dataSetMetadataRepository.save(dataSetMetadata);

            // analyze the updated dataset (not all analysis are performed)
            analyzeDataSet(dataSetId, //
                    false, //
                    asList(ContentAnalysis.class, FormatAnalysis.class, SchemaAnalysis.class));

        } finally {
            lock.unlock();
        }
    }

    @Override
    public Stream<UserDataSetMetadata> search(String name, boolean strict) {

        LOG.debug("search datasets metadata for {}", name);

        final String filter;
        if (strict) {
            filter = "name = '" + name + "'";
        } else {
            filter = "name contains '" + name + "'";
        }
        return dataSetMetadataRepository.list(filter, null, null) //
                .map(d -> conversionService.convert(d, UserDataSetMetadata.class));
    }

    @Override
    public Stream<String> listSupportedEncodings() {
        return encodings.getSupportedCharsets().stream().map(Charset::displayName);
    }

    @Override
    public Object getImportParameters(String importType) {
        DataSetLocation matchingDatasetLocation = locationsService.findLocation(importType);
        Object parametersToReturn;
        if (matchingDatasetLocation == null) {
            parametersToReturn = emptyList();
        } else {
            if (matchingDatasetLocation.isSchemaOriented()) {
                parametersToReturn = matchingDatasetLocation.getParametersAsSchema();
            } else {
                parametersToReturn = matchingDatasetLocation.getParameters();
            }
        }
        return parametersToReturn;
    }

    @Override
    public Object getDataStoreParameters(final String id) {
        DataSetMetadata dataSetMetadata = dataSetMetadataRepository.get(id);
        Object parametersToReturn = null;
        if (dataSetMetadata != null) {
            DataSetLocation matchingDatasetLocation = locationsService
                    .findLocation(dataSetMetadata.getLocation().getLocationType());
            if (matchingDatasetLocation == null) {
                parametersToReturn = emptyList();
            } else {
                if (matchingDatasetLocation.isSchemaOriented()) {
                    ComponentProperties parametersAsSchema = matchingDatasetLocation.getParametersAsSchema();
                    parametersAsSchema.setProperties(dataSetMetadata.getLocation().getParametersAsSchema().getProperties());
                    parametersToReturn = parametersAsSchema;
                } else {
                    parametersToReturn = matchingDatasetLocation.getParameters();
                }
            }
        }
        return parametersToReturn;
    }

    @Override
    public Stream<Import> listSupportedImports() {
        return locationsService.getAvailableLocations().stream() //
                .filter(l -> enabledImports.contains(l.getLocationType())) //
                .filter(DataSetLocation::isEnabled) //
                .map(l -> { //
                    final boolean defaultImport = LocalStoreLocation.NAME.equals(l.getLocationType());
                    ImportBuilder builder = ImportBuilder.builder() //
                            .locationType(l.getLocationType()) //
                            .contentType(l.getAcceptedContentType()) //
                            .defaultImport(defaultImport) //
                            .label(l.getLabel()) //
                            .title(l.getTitle());
                    if (l.isDynamic()) {
                        builder = builder.dynamic(true);
                    } else {
                        builder = builder.dynamic(false).parameters(l.getParameters());
                    }
                    return builder.build();
                }) //
                .sorted((i1, i2) -> { //
                    int i1Value = i1.isDefaultImport() ? 1 : -1;
                    int i2Value = i2.isDefaultImport() ? 1 : -1;
                    final int compare = i2Value - i1Value;
                    if (compare == 0) {
                        // Same level, use location type alphabetical order to determine order.
                        return i1.getLocationType().compareTo(i2.getLocationType());
                    } else {
                        return compare;
                    }
                });
    }

    @Override
    public List<SemanticDomain> getDataSetColumnSemanticCategories(String datasetId, String columnId) {

        LOG.debug("listing semantic categories for dataset #{} column #{}", datasetId, columnId);

        final DataSetMetadata metadata = dataSetMetadataRepository.get(datasetId);
        try (final Stream<DataSetRow> records = contentStore.stream(metadata)) {

            final ColumnMetadata columnMetadata = metadata.getRowMetadata().getById(columnId);
            final Analyzer<Analyzers.Result> analyzer = analyzerService.build(columnMetadata, SEMANTIC);

            analyzer.init();
            records.map(r -> r.get(columnId)).forEach(analyzer::analyze);
            analyzer.end();

            final List<Analyzers.Result> analyzerResult = analyzer.getResult();
            final StatisticsAdapter statisticsAdapter = new StatisticsAdapter(40);
            statisticsAdapter.adapt(singletonList(columnMetadata), analyzerResult);
            LOG.debug("found {} for dataset #{}, column #{}", columnMetadata.getSemanticDomains(), datasetId, columnId);
            return columnMetadata.getSemanticDomains();
        }

    }
}
