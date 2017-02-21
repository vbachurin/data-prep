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

package org.talend.services.dataprep.api;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.Import;
import org.talend.dataprep.api.dataset.statistics.SemanticDomain;
import org.talend.dataprep.api.service.api.EnrichedDataSetMetadata;
import org.talend.dataprep.dataset.service.UserDataSetMetadata;
import org.talend.dataprep.dataset.service.api.UpdateColumnParameters;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.preparation.service.UserPreparation;
import org.talend.dataprep.security.PublicAPI;
import org.talend.dataprep.util.SortAndOrderHelper;

@Service(name = "org.talend.dataprep.DataSetAPI")
public interface DataSetAPI {

    /**
     * Create a dataset from request body content.
     *
     * @param name The dataset name.
     * @param contentType the request content type used to distinguish dataset creation or import.
     * @param dataSetContent the dataset content from the http request body.
     * @return The dataset id.
     */
    @RequestMapping(value = "/api/datasets", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    String create(@RequestParam(defaultValue = "", required = false, name = "name") String name, //
            @RequestParam(defaultValue = "", required = false, name = "tag") String tag, //
            @RequestHeader(name = "Content-Type", required = false) String contentType, //
            @RequestBody InputStream dataSetContent);

    /**
     * Update a data set by id. Create or update a data set based on content provided in PUT body with given id. For documentation
     * purposes, body is typed as 'text/plain' but operation accepts binary content too. Returns the id of the newly created data
     * set.
     *
     * @param name User readable name of the data set (e.g. 'Finance Report 2015', 'Test Data Set').
     * @param id Id of the data set to update / create
     * @param dataSetContent content
     * @return
     */
    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    String createOrUpdateById(@RequestParam(defaultValue = "", required = false, name = "name") String name, //
            @PathVariable("id") String id, //
            InputStream dataSetContent);

    /**
     * Copy the dataset. Copy the dataset, returns the id of the copied created data set.
     *
     * @param name Name of the copy.
     * @param id Id of the data set to copy.
     * @return returns the id of the copied created data set.
     */
    @RequestMapping(value = "/api/datasets/{id}/copy", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    Callable<String> copy(@RequestParam(defaultValue = "", required = false, name = "name") String name, //
            @PathVariable("id") String id);

    /**
     * Update a data set metadata by id. Update a data set metadata based on content provided in PUT body with given id. For
     * documentation purposes. Returns the id of the updated data set metadata.
     *
     * @param id Id of the data set metadata to be updated.
     * @param dataSetContent Data set content.
     */
    @RequestMapping(value = "/api/datasets/{id}/metadata", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    void updateMetadata(@PathVariable("id") String id, DataSetMetadata dataSetContent);

    /**
     * Update a dataset. Update a data set based on content provided in POST body with given id. For documentation purposes, body
     * is typed as 'text/plain' but operation accepts binary content too.
     *
     * @param id Id of the data set to update / create
     * @param dataSetContent content
     * @return The id of the dataset.
     */
    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    String update(@PathVariable("id") String id, InputStream dataSetContent);

    /**
     * Update a dataset. Update a data set based on content provided in POST body with given id. For documentation purposes, body
     * is typed as 'text/plain' but operation accepts binary content too.
     *
     * @param datasetId Id of the dataset to update
     * @param columnId Id of the column to update
     * @param parameters content
     */
    @RequestMapping(value = "/api/datasets/{datasetId}/column/{columnId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    void updateColumn(@PathVariable("datasetId") String datasetId, //
            @PathVariable("columnId") String columnId, //
            @RequestBody UpdateColumnParameters parameters);

    /**
     * Get a data set by id.
     *
     * @param id Id of the data set to get
     * @param fullContent Whether output should be the full data set (true) or not (false).
     * @param includeTechnicalProperties Whether to include internal technical properties (true) or not (false).
     * @return The data set content.
     */
    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    DataSet get(@PathVariable("id") String id, //
            @RequestParam(defaultValue = "false", required = false, name = "fullContent") boolean fullContent, //
            @RequestParam(defaultValue = "false", required = false, name = "includeTechnicalProperties") boolean includeTechnicalProperties);

    /**
     * Return the dataset metadata.
     *
     * @param id the wanted dataset metadata.
     * @return the dataset metadata or no content if not found.
     */
    @RequestMapping(value = "/api/datasets/{id}/metadata", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    DataSetMetadata getMetadata(@PathVariable("id") String id);

    /**
     * Get a data set by id.
     *
     * @param id
     * @param metadata
     * @param sheetName
     * @return The preview for the data set.
     */
    @RequestMapping(value = "/api/datasets/preview/{id}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<StreamingResponseBody> preview(@PathVariable("id") String id, //
            @RequestParam(defaultValue = "true", name = "metadata") boolean metadata, //
            @RequestParam(defaultValue = "", name = "sheetName") String sheetName);

    /**
     * List data sets.
     *
     * @param sort Sort key (by name or date), defaults to 'date'.
     * @param order Order for sort key (desc or asc), defaults to 'desc'.
     * @param name Filter on name containing the specified name
     * @param certified Filter on certified data sets
     * @param favorite Filter on favorite data sets
     * @param limit Filter on recent data sets
     * @return Returns a list of data sets the user can use.
     */
    @RequestMapping(value = "/api/datasets", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    Callable<Stream<UserDataSetMetadata>> list(
            @RequestParam(defaultValue = "creationDate", name = "sort") SortAndOrderHelper.Sort sort, //
            @RequestParam(defaultValue = "desc", name = "order") SortAndOrderHelper.Order order, //
            @RequestParam(defaultValue = "", name = "name") String name, //
            @RequestParam(defaultValue = "false", name = "certified") boolean certified, //
            @RequestParam(defaultValue = "false", name = "favorite") boolean favorite, //
            @RequestParam(defaultValue = "false", name = "limit") boolean limit);

    /**
     * List data sets summary.
     *
     * @param sort Sort key (by name or date), defaults to 'date'.
     * @param order Order for sort key (desc or asc), defaults to 'desc'.
     * @param name Filter on name containing the specified name
     * @param certified Filter on certified data sets
     * @param favorite Filter on favorite data sets
     * @param limit Filter on recent data sets
     * @return Returns a list of data sets summary the user can use.
     */
    @RequestMapping(value = "/api/datasets/summary", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    Callable<Stream<EnrichedDataSetMetadata>> listSummary(
            @RequestParam(defaultValue = "creationDate", name = "sort") SortAndOrderHelper.Sort sort, //
            @RequestParam(defaultValue = "desc", name = "order") SortAndOrderHelper.Order order, //
            @RequestParam(defaultValue = "", name = "name") String name, //
            @RequestParam(defaultValue = "false", name = "certified") boolean certified, //
            @RequestParam(defaultValue = "false", name = "favorite") boolean favorite, //
            @RequestParam(defaultValue = "false", name = "limit") boolean limit);

    /**
     * Returns a list containing all data sets metadata that are compatible with the data set with id <tt>id</tt>. If no
     * compatible data set is found an empty list is returned. The data set with id <tt>dataSetId</tt> is never returned
     * in the list.
     *
     * @param id the specified data set id
     * @param sort the sort criterion: either name or date.
     * @param order the sorting order: either asc or desc
     * @return a list containing all data sets metadata that are compatible with the data set with id <tt>id</tt> and
     * empty list if no data set is compatible.
     */
    @RequestMapping(value = "/api/datasets/{id}/compatibledatasets", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    Callable<Stream<UserDataSetMetadata>> listCompatibleDatasets(@PathVariable("id") String id, //
            @RequestParam(defaultValue = "creationDate", name = "sort") SortAndOrderHelper.Sort sort, //
            @RequestParam(defaultValue = "desc", name = "order") SortAndOrderHelper.Order order);

    /**
     * Returns a list containing all preparations that are compatible with the data set with id <tt>id</tt>. If no
     * compatible preparation is found an empty list is returned.
     *
     * @param id the specified data set id
     * @param sort the sort criterion: either name or date.
     * @param order the sorting order: either asc or desc
     */
    @RequestMapping(value = "/api/datasets/{id}/compatiblepreparations", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    Callable<Stream<UserPreparation>> listCompatiblePreparations(@PathVariable(value = "id") String id, //
            @RequestParam(defaultValue = "lastModificationDate", name = "sort") SortAndOrderHelper.Sort sort, //
            @RequestParam(defaultValue = "desc", name = "order") SortAndOrderHelper.Order order);

    /**
     * Delete a data set.
     *
     * @param id Id of the data set to delete
     */
    @RequestMapping(value = "/api/datasets/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    void delete(@PathVariable("id") String id);

    /**
     * Ask certification for a dataset.
     *
     * @param id Id of the data set to update
     */
    @RequestMapping(value = "/api/datasets/{id}/processcertification", method = RequestMethod.PUT, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    void processCertification(@PathVariable("id") String id);

    /**
     * Suggest actions on a dataset.
     *
     * @param id The dataset id
     * @return The suggested actions for the dataset.
     */
    @RequestMapping(value = "/api/datasets/{id}/actions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody suggestDatasetActions(@PathVariable("id") String id);

    /**
     * Set or Unset the dataset as favorite for the current user.
     *
     * @param id Id of the favorite data set
     * @param unset When true, will remove the dataset from favorites, if false (default) this will set the dataset as favorite.
     * @return The updated dataset.
     */
    @RequestMapping(value = "/api/datasets/favorite/{id}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    Callable<String> favorite(@PathVariable("id") String id, //
            @RequestParam(defaultValue = "false", name = "unset") boolean unset);

    /**
     * List supported dataset encodings.
     *
     * @return Returns the supported dataset encodings.
     */
    @RequestMapping(value = "/api/datasets/encodings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    Callable<Stream<String>> listEncodings();

    /**
     * Fetch the parameters needed to imports a dataset.
     *
     * @param importType Import type (CSV...)
     * @return Returns the parameters needed to imports a dataset.
     */
    @RequestMapping(value = "/api/datasets/imports/{import}/parameters", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    ResponseEntity<StreamingResponseBody> getImportParameters(@PathVariable("import") String importType);

    /**
     * List supported imports for a dataset.
     *
     * @return Returns the supported import types.
     */
    @RequestMapping(value = "/api/datasets/imports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    Callable<Stream<Import>> listImports();

    /**
     * Return the semantic types for a given dataset / column.
     *
     * @param datasetId the dataset id.
     * @param columnId the column id.
     * @return the semantic types for a given dataset / column.
     */
    @RequestMapping(value = "/api/datasets/{datasetId}/columns/{columnId}/types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    Callable<Stream<SemanticDomain>> getDataSetColumnSemanticCategories(@PathVariable("datasetId") String datasetId, //
            @PathVariable("columnId") String columnId);
}
