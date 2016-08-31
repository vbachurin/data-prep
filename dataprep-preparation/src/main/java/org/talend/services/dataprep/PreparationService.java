package org.talend.services.dataprep;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Collection;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.PreparationDetails;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;

/**
 * This service provides all operations needed to organize / create preparations (i.e. a list of actions to apply to a content) in
 * the Data Prep.
 */
@Service(name = "PreparationService")
public interface PreparationService {

    /**
     * Create a preparation from the http request body.
     *
     * @param preparation the preparation to create.
     * @param folder where to store the preparation.
     * @return the created preparation id.
     */
    @RequestMapping(value = "/preparations", method = POST, produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Timed
    String create(@RequestBody Preparation preparation, @RequestParam() String folder);

    /**
     * List all the preparations id.
     *
     * @param sort how the preparation should be sorted (default is 'last modification date').
     * @param order how to apply the sort.
     * @return the preparations id list.
     */
    @RequestMapping(value = "/preparations", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    List<String> list(@RequestParam(defaultValue = "MODIF", required = false) String sort,
            @RequestParam(defaultValue = "DESC", required = false) String order);

    /**
     * List all preparation details.
     *
     * @param sort how to sort the preparations.
     * @param order how to order the sort.
     * @return the preparation details.
     */
    @RequestMapping(value = "/preparations/details", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Collection<PreparationDetails> listAll(@RequestParam(defaultValue = "MODIF", required = false) String sort,
            @RequestParam(defaultValue = "DESC", required = false) String order);

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
     * <li>folder path</li>
     * </ul>
     * </p>
     *
     * @param dataSetId to search all preparations based on this dataset id.
     * @param folder to search all preparations located in this folder.
     * @param name to search all preparations that match this name.
     * @param exactMatch if true, the name matching must be exact.
     * @param sort Sort key (by name, creation date or modification date).
     * @param order Order for sort key (desc or asc).
     */
    @RequestMapping(value = "/preparations/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Iterable<PreparationDetails> searchPreparations(@RequestParam(required = false) String dataSetId,
            @RequestParam(required = false) String folder, @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "true") boolean exactMatch, @RequestParam(defaultValue = "MODIF") String sort,
            @RequestParam(defaultValue = "DESC") String order);

    /**
     * Copy the given preparation to the given name / folder ans returns the new if in the response.
     *
     * @param name the name of the copied preparation, if empty, the name is "orginal-preparation-name Copy"
     * @param destination the folder path where to copy the preparation, if empty, the copy is in the same folder.
     * @return The new preparation id.
     */
    @RequestMapping(value = "/preparations/{id}/copy", method = POST, produces = TEXT_PLAIN_VALUE)
    @Timed
    String copy(@PathVariable(value = "id") String preparationId, @RequestParam(required = false) String name,
            @RequestParam() String destination);

    /**
     * Move a preparation to an other folder.
     *
     * @param folder The original folder of the preparation.
     * @param destination The new folder of the preparation.
     * @param newName The new preparation name.
     */
    @RequestMapping(value = "/preparations/{id}/move", method = PUT, produces = TEXT_PLAIN_VALUE)
    @Timed
    void move(@PathVariable(value = "id") String preparationId, @RequestParam String folder, @RequestParam String destination,
            @RequestParam(defaultValue = "", required = false) String newName);

    /**
     * Delete a preparation content based on provided id. Id should be a UUID returned by the list operation. Not valid or non
     * existing preparation id returns empty content.
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
    String update(@PathVariable("id") String id, @RequestBody Preparation preparation);

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
    void copyStepsFrom(@PathVariable("id") String id, @RequestParam String from);

    /**
     * Return a preparation details.
     *
     * @param id the wanted preparation id.
     * @return the preparation details.
     */
    @RequestMapping(value = "/preparations/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    PreparationDetails get(@PathVariable("id") String id);

    /**
     * Return the folder that holds this preparation.
     *
     * @param id the wanted preparation id.
     * @return the folder that holds this preparation.
     */
    @RequestMapping(value = "/preparations/{id}/folder", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Folder searchLocation(@PathVariable("id") String id);

    /**
     * Get all preparation steps id.
     * 
     * @param id A preparation id.
     * @return Return the steps of the preparation with provided id.
     */
    @RequestMapping(value = "/preparations/{id}/steps", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    List<String> getSteps(@PathVariable("id") String id);

    /**
     * Append step(s) in a preparation.
     */
    @RequestMapping(value = "/preparations/{id}/actions", method = POST, consumes = APPLICATION_JSON_VALUE)
    @Timed
    void appendSteps(@PathVariable("id") String id, @RequestBody List<AppendStep> stepsToAppend);

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
    void updateAction(@PathVariable("id") String preparationId, @PathVariable("stepId") String stepToModifyId,
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
    void deleteAction(@PathVariable("id") String id, @PathVariable("stepId") String stepToDeleteId);

    @RequestMapping(value = "/preparations/{id}/head/{headId}", method = PUT)
    @Timed
    void setPreparationHead(@PathVariable("id") String preparationId, //
            @PathVariable("headId") String headId);

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

    @RequestMapping(value = "/preparations/{preparationId}/lock", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    void lockPreparation(@PathVariable("preparationId") String preparationId);

    @RequestMapping(value = "/preparations/{preparationId}/unlock", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    void unlockPreparation(@PathVariable("preparationId") String preparationId);

    /**
     * Check if dataset is used by a preparation.
     * 
     * @param datasetId A data set id.
     * @return Returns no content, the response code is the meaning.
     */
    @RequestMapping(value = "/preparations/use/dataset/{datasetId}", method = HEAD)
    @Timed
    ResponseEntity<Void> preparationsThatUseDataset(@PathVariable("datasetId") String datasetId);
}
