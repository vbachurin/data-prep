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

import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.preparation.*;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.preparation.service.UserPreparation;
import org.talend.dataprep.util.SortAndOrderHelper.Order;
import org.talend.dataprep.util.SortAndOrderHelper.Sort;

/**
 * Operations on preparations
 */
@Service(name = "dataprep.PreparationService")
public interface PreparationService {

    /**
     * Create a preparation from the http request body.
     *
     * @param preparation the preparation to create.
     * @param folderId where to store the preparation.
     * @return the created preparation id.
     */
    @RequestMapping(value = "/preparations", method = POST, produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    String create(@RequestBody Preparation preparation, @RequestParam(name = "folderId") String folderId);

    /**
     * List all the preparations id.
     *
     * @param sort how the preparation should be sorted (default is 'last modification date').
     * @param order how to apply the sort.
     * @return the preparations id list.
     */
    @RequestMapping(value = "/preparations", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Stream<String> list(@RequestParam(defaultValue = "lastModificationDate", name = "sort") Sort sort,
            @RequestParam(defaultValue = "desc", name = "order") Order order);

    /**
     * List all preparation details.
     *
     * @param sort how to sort the preparations.
     * @param order how to order the sort.
     * @return the preparation details.
     */
    @RequestMapping(value = "/preparations/details", method = GET)
    @Timed
    Stream<UserPreparation> listAll(@RequestParam(defaultValue = "lastModificationDate", name = "sort") Sort sort,
            @RequestParam(defaultValue = "desc", name = "order") Order order);

    /**
     * List all preparation summaries.
     *
     * @return the preparation summaries, sorted by descending last modification date.
     */
    @RequestMapping(value = "/preparations/summaries", method = GET)
    @Timed
    Stream<PreparationSummary> listSummary();

    /**
     * <p>
     * Search preparation entry point.
     * </p>
     * <p>
     * <p>
     * So far at least one search criteria can be processed at a time among the following ones :
     * <ul>
     * <li>dataset id</li>
     * <li>preparation name & exact match</li>
     * <li>folderId path</li>
     * </ul>
     * </p>
     *
     * @param dataSetId to search all preparations based on this dataset id.
     * @param folderId to search all preparations located in this folderId.
     * @param name to search all preparations that match this name.
     * @param exactMatch if true, the name matching must be exact.
     * @param sort Sort key (by name, creation date or modification date).
     * @param order Order for sort key (desc or asc).
     */
    @RequestMapping(value = "/preparations/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Stream<UserPreparation> searchPreparations(@RequestParam(required = false, name = "dataSetId") String dataSetId,
            @RequestParam(required = false, name = "folderId") String folderId,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(defaultValue = "true", name = "exactMatch") boolean exactMatch,
            @RequestParam(defaultValue = "lastModificationDate", name = "sort") Sort sort,
            @RequestParam(defaultValue = "desc", name = "order") Order order);

    /**
     * Copy the given preparation to the given name / folder ans returns the new if in the response.
     *
     * @param name the name of the copied preparation, if empty, the name is "orginal-preparation-name Copy"
     * @param destination the folder path where to copy the preparation, if empty, the copy is in the same folder.
     * @return The new preparation id.
     */
    @RequestMapping(value = "/preparations/{id}/copy", method = POST, produces = TEXT_PLAIN_VALUE)
    @Timed
    String copy(@PathVariable(value = "id") String preparationId, @RequestParam(required = false, name = "name") String name,
            @RequestParam(name = "destination") String destination);

    /**
     * Move a preparation to an other folder.
     *
     * @param folder The original folder of the preparation.
     * @param destination The new folder of the preparation.
     * @param newName The new preparation name.
     */
    @RequestMapping(value = "/preparations/{id}/move", method = PUT, produces = TEXT_PLAIN_VALUE)
    @Timed
    void move(@PathVariable(value = "id") String preparationId, @RequestParam(name = "folder") String folder,
            @RequestParam(name = "destination") String destination,
            @RequestParam(defaultValue = "", name = "newName") String newName);

    /**
     * Delete the preparation that match the given id.
     *
     * @param id the preparation id to delete.
     */
    @RequestMapping(value = "/preparations/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    void delete(@PathVariable(value = "id") String id);

    /**
     * Update a preparation.
     *
     * @param id the preparation id to update.
     * @param preparation the updated preparation.
     * @return the updated preparation id.
     */
    @RequestMapping(value = "/preparations/{id}", method = PUT, produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Timed
    String update(@PathVariable("id") String id, PreparationMessage preparation);

    /**
     * Update a preparation steps.
     *
     * @param preparationId the preparation id (mainly for to check).
     * @param steps the steps to update.
     * @return the updated preparation id.
     */
    @RequestMapping(value = "/preparations/{preparationId}/steps", method = PUT, produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Timed
    String updateStepMetadata(@PathVariable("preparationId") String preparationId, @RequestBody List<Step> steps);

    /**
     * Copy the steps from the another preparation to this one.
     * <p>
     * This is only allowed if this preparation has no steps.
     *
     * @param id the preparation id to update.
     * @param from the preparation id to copy the steps from.
     */
    @RequestMapping(value = "/preparations/{id}/steps/copy", method = PUT, produces = TEXT_PLAIN_VALUE)
    @Timed
    void copyStepsFrom(@PathVariable("id") String id, @RequestParam(name = "from") String from);

    /**
     * Return a preparation details.
     *
     * @param id the wanted preparation id.
     * @return the preparation details.
     */
    @RequestMapping(value = "/preparations/{id}/details", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    PreparationMessage getDetails(@PathVariable("id") String id);

    /**
     * Return a preparation.
     *
     * @param id the wanted preparation id.
     * @return the preparation details.
     */
    @RequestMapping(value = "/preparations/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Preparation get(@PathVariable("id") String id);

    /**
     * Return the folder that holds this preparation.
     *
     * @param id the wanted preparation id.
     * @return the folder that holds this preparation.
     */
    @RequestMapping(value = "/preparations/{id}/folder", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Folder searchLocation(@PathVariable("id") String id);

    @RequestMapping(value = "/preparations/{id}/steps", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    List<String> getSteps(@PathVariable("id") String id);

    /**
     * Update a step in a preparation <b>Strategy</b><br/>
     * The goal here is to rewrite the preparation history from 'the step to modify' (STM) to the head, with STM
     * containing the new action.<br/>
     * <ul>
     * <li>1. Extract the actions from STM (excluded) to the head</li>
     * <li>2. Insert the new actions before the other extracted actions. The actions list contains all the actions from
     * the <b>NEW</b> STM to the head</li>
     * <li>3. Set preparation head to STM's parent, so STM will be excluded</li>
     * <li>4. Append each action (one step is created by action) after the new preparation head</li>
     * </ul>
     */
    @RequestMapping(value = "/preparations/{id}/actions/{stepId}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    @Timed
    void updateAction(@PathVariable("id") String preparationId, @PathVariable("stepId") String stepId,
            @RequestBody AppendStep newStep);

    /**
     * Delete a step in a preparation.<br/>
     * STD : Step To Delete <br/>
     * <br/>
     * <ul>
     * <li>1. Extract the actions from STD (excluded) to the head. The actions list contains all the actions from the
     * STD's child to the head.</li>
     * <li>2. Filter the preparations that apply on a column created by the step to delete. Those steps will be removed
     * too.</li>
     * <li>2bis. Change the actions that apply on columns > STD last created column id. The created columns ids after
     * the STD are shifted.</li>
     * <li>3. Set preparation head to STD's parent, so STD will be excluded</li>
     * <li>4. Append each action after the new preparation head</li>
     * </ul>
     */
    @RequestMapping(value = "/preparations/{id}/actions/{stepId}", method = DELETE)
    @Timed
    void deleteAction(@PathVariable("id") String id, @PathVariable("stepId") String stepId);

    /**
     * Move preparation head
     *
     * @param preparationId
     * @param headId
     */
    @RequestMapping(value = "/preparations/{id}/head/{headId}", method = PUT)
    @Timed
    void setPreparationHead(@PathVariable("id") String preparationId, @PathVariable("headId") String headId);

    /**
     * Get all the actions of a preparation at given version.
     *
     * @param id the wanted preparation id.
     * @param version the wanted preparation version.
     * @return the list of actions.
     */
    @RequestMapping(value = "/preparations/{id}/actions/{version}", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    List<Action> getVersionedAction(@PathVariable("id") String id, @PathVariable("version") String version);

    /**
     * List all preparation related error codes.
     */
    @RequestMapping(value = "/preparations/errors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    Iterable<JsonErrorCodeDescription> listErrors();

    /**
     * Lock the specified preparation.
     *
     * @param preparationId
     */
    @RequestMapping(value = "/preparations/{preparationId}/lock", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    void lockPreparation(@PathVariable("preparationId") String preparationId);

    /**
     * Unlock the specified preparation.
     *
     * @param preparationId
     */
    @RequestMapping(value = "/preparations/{preparationId}/unlock", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    void unlockPreparation(@PathVariable("preparationId") String preparationId);

    /**
     * Check if dataset is used by a preparation.
     *
     * @param datasetId
     * @return
     */
    @RequestMapping(value = "/preparations/use/dataset/{datasetId}", method = HEAD)
    @Timed
    ResponseEntity<Void> preparationsThatUseDataset(@PathVariable("datasetId") String datasetId);

    /**
     * Moves a step within a preparation after a specified step
     *
     * @param preparationId
     * @param stepId
     * @param parentStepId
     */
    @RequestMapping(value = "/preparations/{id}/steps/{stepId}/order", method = POST)
    @Timed
    void moveStep(@PathVariable("id") String preparationId, @PathVariable("stepId") String stepId,
            @RequestParam(name = "parentStepId") String parentStepId);

    /**
     * Adds an action at the end of preparation. Does not return any value, client may expect successful operation based on HTTP
     * status code.
     *
     * @param id
     * @param steps
     */
    @RequestMapping(value = "/preparations/{id}/actions", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Timed
    void addPreparationAction(@PathVariable(value = "id") String id, @RequestBody List<AppendStep> steps);

    /**
     * Retrieve a specific step.
     *
     * @param stepId
     * @return
     */
    @RequestMapping(value = "/steps/{id}", method = GET)
    @Timed
    Step getStep(@PathVariable("id") String stepId);
}
