package org.talend.services.dataprep;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.service.api.Import;
import org.talend.dataprep.dataset.service.api.UpdateColumnParameters;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.security.PublicAPI;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * The Data Set service handles all operations related to data sets (i.e. sources of data in Data Prep).
 */
@Service(name = "DataSetService")
public interface DataSetService {

    String CONTENT_TYPE = "Content-Type";

    /**
     * List all data sets and filters on certified, or favorite or a limited number when asked.
     *
     * @param sort Sort key (by name, creation or modification date)
     * @param order Order for sort key (desc or asc or modif)
     * @param name Filter on name containing the specified name
     * @param certified Filter on certified data sets
     * @param favorite Filter on favorite data sets
     * @param limit Only return a limited number of data sets
     * @return Returns the list of data sets (and filters) the current user is allowed to see. Creation date is a Epoch time value
     * (in UTC time zone).
     */
    @RequestMapping(value = "/datasets", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<DataSetMetadata> list(@RequestParam(defaultValue = "DATE") String sort, //
            @RequestParam(defaultValue = "DESC") String order, //
            @RequestParam(defaultValue = "") String name, //
            @RequestParam(defaultValue = "false") boolean certified, //
            @RequestParam(defaultValue = "false") boolean favorite, //
            @RequestParam(defaultValue = "false", required = false) boolean limit);

    /**
     * Returns a list containing all data sets that are compatible with the data set with id <tt>dataSetId</tt>. If no
     * compatible data set is found an empty list is returned. The data set with id <tt>dataSetId</tt> is never returned
     * in the list.
     *
     * @param dataSetId the specified data set id
     * @param sort the sort criterion: either name or date.
     * @param order the sorting order: either asc or desc.
     * @return a list containing all data sets that are compatible with the data set with id <tt>dataSetId</tt> and
     * empty list if no data set is compatible.
     */
    @RequestMapping(value = "/datasets/{id}/compatibledatasets", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<DataSetMetadata> listCompatibleDatasets(@PathVariable(value = "id") String dataSetId, //
            @RequestParam(defaultValue = "DATE", required = false) String sort, //
            @RequestParam(defaultValue = "DESC", required = false) String order);

    /**
     * Creates a new data set and returns the new data set id as text in the response.
     *
     * @param name An optional name for the new data set (might be <code>null</code>).
     * @param contentType the request content type.
     * @param content The raw content of the data set (might be a CSV, XLS...) or the connection parameter in case of a
     * remote csv.
     * @return The new data id.
     * @see #get(boolean, String)
     */
    @RequestMapping(value = "/datasets", method = POST, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    @VolumeMetered
    String create(@RequestParam(defaultValue = "") String name, //
            @RequestParam(defaultValue = "") String tag, //
            @RequestHeader(CONTENT_TYPE) String contentType, //
            InputStream content) throws IOException;

    /**
     * Returns the <b>full</b> data set content for given id.
     *
     * @param metadata If <code>true</code>, includes data set metadata information.
     * @param dataSetId A data set id.
     * @return The full data set.
     */
    @RequestMapping(value = "/datasets/{id}/content", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Callable<DataSet> get(@RequestParam(defaultValue = "true") boolean metadata, @PathVariable(value = "id") String dataSetId);

    /**
     * Returns the data set {@link DataSetMetadata metadata} for given <code>dataSetId</code>.
     *
     * @param dataSetId A data set id. If <code>null</code> <b>or</b> if no data set with provided id exits, operation
     * returns {@link org.apache.commons.httpclient.HttpStatus#SC_NO_CONTENT} if metadata does not exist.
     */
    @RequestMapping(value = "/datasets/{id}/metadata", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    DataSet getMetadata(@PathVariable(value = "id") String dataSetId);

    /**
     * Deletes a data set with provided id.
     *
     * @param dataSetId A data set id. If data set id is unknown, no exception nor status code to indicate this is set.
     */
    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    void delete(@PathVariable(value = "id") String dataSetId);

    /**
     * Copy this dataset to a new one and returns the new data set id as text in the response.
     *
     * @param copyName the name of the copy
     * @return The new data id.
     */
    @RequestMapping(value = "/datasets/{id}/copy", method = POST, produces = TEXT_PLAIN_VALUE)
    @Timed
    String copy(@PathVariable(value = "id") String dataSetId, @RequestParam(required = false) String copyName);

    /**
     * Ask certification for a dataset.
     * 
     * @param dataSetId Id of the data set to update.
     */
    @RequestMapping(value = "/datasets/{id}/processcertification", method = PUT, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @ApiOperation(value = "", notes = "Advance certification step of this dataset.")
    @Timed
    void processCertification(@PathVariable(value = "id") String dataSetId);

    /**
     * Updates a data set content and metadata. If no data set exists for given id, data set is silently created.
     *
     * @param dataSetId The id of data set to be updated.
     * @param name The new name for the data set. Empty name (or <code>null</code>) does not update dataset name.
     * @param dataSetContent The new content for the data set. If empty, existing content will <b>not</b> be replaced.
     * For delete operation, look at {@link #delete(String)}.
     */
    @RequestMapping(value = "/datasets/{id}/raw", method = PUT, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    @VolumeMetered
    void updateRawDataSet(@PathVariable(value = "id") String dataSetId, //
            @RequestParam(value = "name", required = false) String name, //
            @ApiParam(value = "content") InputStream dataSetContent);

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
     * @param dataSetId A data set id.
     */
    @RequestMapping(value = "/datasets/{id}/preview", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    DataSet preview(@RequestParam(defaultValue = "true") boolean metadata, //
            @RequestParam(defaultValue = "") String sheetName, //
            @PathVariable(value = "id") String dataSetId //
    );

    /**
     * Updates a data set metadata. If no data set exists for given id, a {@link TDPException} is thrown.
     *
     * @param dataSetId The id of data set to be updated.
     * @param dataSetMetadata The new content for the data set. If empty, existing content will <b>not</b> be replaced.
     * For delete operation, look at {@link #delete(String)}.
     */
    @RequestMapping(value = "/datasets/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    @Timed
    void updateDataSet(@PathVariable(value = "id") String dataSetId, @RequestBody DataSetMetadata dataSetMetadata);

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
     * @param dataSetId, the id of the favorites data set. If the data set does not exists nothing is done.
     */
    @RequestMapping(value = "/datasets/{id}/favorite", method = PUT, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    void setFavorites(@RequestParam(defaultValue = "false") boolean unset, @PathVariable(value = "id") String dataSetId);

    /**
     * Update the column of the data set and computes the
     *
     * @param dataSetId the dataset id.
     * @param columnId the column id.
     * @param parameters the new type and domain.
     */
    @RequestMapping(value = "/datasets/{datasetId}/column/{columnId}", method = POST, consumes = APPLICATION_JSON_VALUE)
    @Timed
    void updateDatasetColumn(@PathVariable(value = "datasetId") String dataSetId,
            @PathVariable(value = "columnId") String columnId, @RequestBody UpdateColumnParameters parameters);

    /**
     * Search datasets.
     *
     * @param name what to searched in datasets.
     * @param strict If the searched name should be the full name
     * @return the list of found datasets metadata.
     */
    @RequestMapping(value = "/datasets/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<DataSetMetadata> search(@RequestParam String name, @RequestParam boolean strict);

    /**
     * List the supported encodings for dataset
     * 
     * @return A list can be used by user to select a different encoding.
     */
    @RequestMapping(value = "/datasets/encodings", method = GET, consumes = MediaType.ALL_VALUE, produces = APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    List<String> listSupportedEncodings();

    /**
     * Returns all the parameter requested for a given import type. Import type should be one of {@link #listSupportedImports()}.
     * 
     * @param importType An import type as returned by {@link Import#getLabel()}.
     * @return The parameters for the given import type.
     */
    @RequestMapping(value = "/datasets/imports/{import}/parameters", method = GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    List<Parameter> getImportParameters(@PathVariable("import") String importType);

    /**
     * List the supported imports for dataset.
     * 
     * @return A list can be used by user to create a data set.
     */
    @RequestMapping(value = "/datasets/imports", method = GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    List<Import> listSupportedImports();
}
