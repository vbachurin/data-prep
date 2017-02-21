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

package org.talend.services.dataprep;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.Import;
import org.talend.dataprep.api.dataset.statistics.SemanticDomain;
import org.talend.dataprep.dataset.service.UserDataSetMetadata;
import org.talend.dataprep.dataset.service.api.UpdateColumnParameters;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.security.PublicAPI;
import org.talend.dataprep.util.SortAndOrderHelper.Order;
import org.talend.dataprep.util.SortAndOrderHelper.Sort;

/**
 * Operations on data sets
 */
@Service(name = "dataprep.DataSetService")
public interface DataSetService {

    /**
     * List all data sets and filters on certified, or favorite or a limited number when asked.
     *
     * @param sort Sort key (by name, creation or modification date)
     * @param order Order for sort key ("desc" or "asc" or "modif").
     * @param name Filter on name containing the specified name.
     * @param certified Filter on certified data sets.
     * @param favorite Filter on favorite data sets.
     * @param limit Only return a limited number of data sets.
     * @return Returns the list of data sets (and filters) the current user is allowed to see. Creation date is a Epoch time value
     * (in UTC time zone).
     */
    @RequestMapping(value = "/datasets", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Callable<Stream<UserDataSetMetadata>> list(@RequestParam(defaultValue = "creationDate", name = "sort") Sort sort,
            @RequestParam(defaultValue = "desc", name = "order") Order order,
            @RequestParam(defaultValue = "", name = "name") String name,
            @RequestParam(defaultValue = "false", name = "certified") boolean certified,
            @RequestParam(defaultValue = "false", name = "favorite") boolean favorite,
            @RequestParam(defaultValue = "false", name = "limit") boolean limit);

    /**
     * Returns a list containing all data sets that are compatible with the data set with id <tt>dataSetId</tt>. If no
     * compatible data set is found an empty list is returned. The data set with id <tt>dataSetId</tt> is never returned
     * in the list.
     *
     * @param id the specified data set id
     * @param sort the sort criterion: either name or date.
     * @param order the sorting order: either asc or desc.
     * @return a list containing all data sets that are compatible with the data set with id <tt>dataSetId</tt> and
     * empty list if no data set is compatible.
     */
    @RequestMapping(value = "/datasets/{id}/compatibledatasets", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<UserDataSetMetadata> listCompatibleDatasets(@PathVariable(value = "id") String id,
            @RequestParam(defaultValue = "creationDate", name = "sort") Sort sort,
            @RequestParam(defaultValue = "desc", name = "order") Order order);

    /**
     * Creates a new data set and returns the new data set id as text in the response.
     *
     * @param name An optional name for the new data set (might be <code>null</code>).
     * @param contentType the request content type.
     * @param content The raw content of the data set (might be a CSV, XLS...) or the connection parameter in case of a
     * remote csv.
     * @return The new data id.
     * @see DataSetService#get(boolean, boolean, String)
     */
    @RequestMapping(value = "/datasets", method = POST, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    @VolumeMetered
    String create(@RequestParam(defaultValue = "", name = "name") String name, //
            @RequestParam(defaultValue = "", name = "tag") String tag, //
            @RequestHeader(name = "Content-Type", required = false) String contentType, //
            InputStream content) throws IOException;

    /**
     * Returns the <b>full</b> data set content for given id.
     *
     * @param metadata If <code>true</code>, includes data set metadata information.
     * @param id A data set id.
     * @return The full data set.
     */
    @RequestMapping(value = "/datasets/{id}/content", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    DataSet get(@RequestParam(defaultValue = "true", name = "metadata") boolean metadata, //
            @RequestParam(defaultValue = "false", name = "includeInternalContent") boolean includeInternalContent, //
            @PathVariable(name = "id") String id);

    /**
     * Returns the data set {@link DataSetMetadata metadata} for given <code>dataSetId</code>.
     *
     * @param id A data set id. If <code>null</code> <b>or</b> if no data set with provided id exits, operation
     * returns {@link org.apache.commons.httpclient.HttpStatus#SC_NO_CONTENT} if metadata does not exist.
     */
    @RequestMapping(value = "/datasets/{id}/metadata", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    DataSetMetadata getMetadata(@PathVariable(value = "id") String id);

    /**
     * Deletes a data set with provided id.
     *
     * @param id A data set id. If data set id is unknown, no exception nor status code to indicate this is set.
     */
    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    void delete(@PathVariable(value = "id") String id);

    /**
     * Copy this dataset to a new one and returns the new data set id as text in the response.
     *
     * @param copyName the name of the copy
     * @return The new data id.
     */
    @RequestMapping(value = "/datasets/{id}/copy", method = POST, produces = TEXT_PLAIN_VALUE)
    @Timed
    String copy(@PathVariable(value = "id") String dataSetId, @RequestParam(required = false, name = "copyName") String copyName);

    @RequestMapping(value = "/datasets/{id}/processcertification", method = PUT, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    void processCertification(@PathVariable(value = "id") String id);

    /**
     * Updates a data set content and metadata. If no data set exists for given id, data set is silently created.
     *
     * @param id The id of data set to be updated.
     * @param name The new name for the data set. Empty name (or <code>null</code>) does not update dataset name.
     * @param dataSetContent The new content for the data set. If empty, existing content will <b>not</b> be replaced.
     * For delete operation, look at {@link #delete(String)}.
     */
    @RequestMapping(value = "/datasets/{id}/raw", method = PUT, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    @VolumeMetered
    void updateRawDataSet(@PathVariable(value = "id") String id, //
            @RequestParam(value = "name", required = false) String name, //
            InputStream dataSetContent);

    /**
     * List all dataset related error codes.
     */
    @RequestMapping(value = "/datasets/errors", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<JsonErrorCodeDescription> listErrors();

    /**
     * Returns preview of the the data set content for given id (first 100 rows). Service might return
     * {@link org.apache.commons.httpclient.HttpStatus#SC_ACCEPTED} if the data set exists but analysis is not yet fully
     * completed so content is not yet ready to be served.
     *
     * @param metadata If <code>true</code>, includes data set metadata information.
     * @param sheetName the sheet name to preview
     * @param id A data set id.
     */
    @RequestMapping(value = "/datasets/{id}/preview", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    DataSet preview(@RequestParam(defaultValue = "true", name = "metadata") boolean metadata, //
            @RequestParam(defaultValue = "", name = "sheetName") String sheetName, //
            @PathVariable(value = "id") String id);

    /**
     * Updates a data set metadata. If no data set exists for given id, a {@link TDPException} is thrown.
     *
     * @param id The id of data set to be updated.
     * @param dataSetMetadata The new content for the data set. If empty, existing content will <b>not</b> be replaced.
     * For delete operation, look at {@link #delete(String)}.
     */
    @RequestMapping(value = "/datasets/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    @Timed
    void updateDataSet(@PathVariable(value = "id") String id, @RequestBody DataSetMetadata dataSetMetadata);

    /**
     * list all the favorites dataset for the current user
     *
     * @return a list of the dataset Ids of all the favorites dataset for the current user or an empty list if none
     * found
     */
    @RequestMapping(value = "/datasets/favorites", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<String> favorites();

    /**
     * update the current user data dataset favorites list by adding or removing the dataSetId according to the unset
     * flag. The user data for the current will be created if it does not exist. If no data set exists for given id, a
     * {@link TDPException} is thrown.
     *
     * @param unset, if true this will remove the dataSetId from the list of favorites, if false then it adds the
     * dataSetId to the favorite list
     * @param id, the id of the favorites data set. If the data set does not exists nothing is done.
     */
    @RequestMapping(value = "/datasets/{id}/favorite", method = PUT, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    void setFavorites(@RequestParam(defaultValue = "false", name = "unset") boolean unset, @PathVariable(value = "id") String id);

    /**
     * Update the column of the data set and computes the
     *
     * @param dataSetId the dataset id.
     * @param columnId the column id.
     * @param parameters the new type and domain.
     */
    @RequestMapping(value = "/datasets/{datasetId}/column/{columnId}", method = POST, consumes = APPLICATION_JSON_VALUE)
    @Timed
    void updateDatasetColumn(@PathVariable(value = "datasetId") final String dataSetId,
            @PathVariable(value = "columnId") final String columnId, @RequestBody final UpdateColumnParameters parameters);

    /**
     * Search datasets.
     *
     * @param name what to searched in datasets.
     * @param strict If the searched name should be the full name
     * @return the list of found datasets metadata.
     */
    @RequestMapping(value = "/datasets/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Stream<UserDataSetMetadata> search(@RequestParam(name = "name") final String name,
            @RequestParam(name = "strict") final boolean strict);

    /**
     * List the supported encodings for dataset.
     *
     * @return This list can be used by user to change dataset encoding.
     */
    @RequestMapping(value = "/datasets/encodings", method = GET, consumes = MediaType.ALL_VALUE, produces = APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    Stream<String> listSupportedEncodings();

    /**
     * Get the import parameters for <tt>import</tt>.
     *
     * @param importType One of dataset support import types.
     * @return Import parameters
     */
    @RequestMapping(value = "/datasets/imports/{import}/parameters", method = GET, consumes = MediaType.ALL_VALUE, produces = APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    // This method have to return Object because it can either return the legacy List<Parameter> or the new TComp oriented
    // ComponentProperties
    Object getImportParameters(@PathVariable("import") final String importType);

    /**
     * Get the dataset import parameters.
     */
    @RequestMapping(value = "/datasets/{id}/datastore/properties", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    // This method have to return Object because it can either return the legacy List<Parameter> or the new TComp oriented
    // ComponentProperties
    Object getDataStoreParameters(@PathVariable("id") final String dataSetId);

    @RequestMapping(value = "/datasets/imports", method = GET, consumes = MediaType.ALL_VALUE, produces = APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    Stream<Import> listSupportedImports();

    /**
     * Return the semantic types for a given dataset / column.
     *
     * @param datasetId the datasetId id.
     * @param columnId the column id.
     * @return the semantic types for a given dataset / column.
     */
    @RequestMapping(value = "/datasets/{datasetId}/columns/{columnId}/types", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    List<SemanticDomain> getDataSetColumnSemanticCategories(@PathVariable("datasetId") String datasetId,
            @PathVariable("columnId") String columnId);
}
