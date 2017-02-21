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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.api.PreviewAddParameters;
import org.talend.dataprep.api.service.api.PreviewDiffParameters;
import org.talend.dataprep.api.service.api.PreviewUpdateParameters;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.security.PublicAPI;
import org.talend.dataprep.util.SortAndOrderHelper;

@Service(name = "dataprep.PreparationAPI")
public interface PreparationAPI {

    /**
     * Get all preparations.
     *
     * @param format Format of the returned document (can be 'long', 'short' or 'summary'). Defaults to 'summary'.
     * @param sort Sort key, defaults to 'modification'.
     * @param order Order for sort key (desc or asc), defaults to 'desc'.
     * @return Returns the list of preparations the current user is allowed to see.
     */
    @RequestMapping(value = "/api/preparations", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<StreamingResponseBody> listPreparations(@RequestParam(defaultValue = "summary", name = "format") String format,
            @RequestParam(defaultValue = "lastModificationDate", name = "sort") SortAndOrderHelper.Sort sort,
            @RequestParam(defaultValue = "desc", name = "order") SortAndOrderHelper.Order order);

    /**
     * Returns a list containing all data sets metadata that are compatible with a preparation identified by
     * <tt>preparationId</tt>: its id. If no compatible data set is found an empty list is returned. The base data set
     * of the preparation with id <tt>preparationId</tt> is never returned in the list.
     *
     * @param preparationId the specified preparation id
     * @param sort the sort criterion: either name or date.
     * @param order the sorting order: either asc or desc
     */
    @RequestMapping(value = "/api/preparations/{id}/basedatasets", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody listCompatibleDatasets(@PathVariable(value = "id") String preparationId,
            @RequestParam(defaultValue = "creationDate", required = false, name = "sort") SortAndOrderHelper.Sort sort,
            @RequestParam(defaultValue = "desc", required = false, name = "order") SortAndOrderHelper.Order order);

    /**
     * Create a new preparation for preparation content in body.
     *
     * @param folder Where to store the preparation.
     * @param preparation The original preparation. You may set all values, service will override values you can't write to.
     * @return Returns the created preparation id.
     */
    @RequestMapping(value = "/api/preparations", method = POST, consumes = APPLICATION_JSON_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    String createPreparation(@RequestParam(name = "folder") String folder, @RequestBody Preparation preparation);

    /**
     * Update a preparation with content in body.
     *
     * @param id The id of the preparation to update.
     * @param preparation The updated preparation. Null values are ignored during update. You may set all values, service will
     * override values you can't write to.
     * @return Returns the updated preparation id.
     */
    @RequestMapping(value = "/api/preparations/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    String updatePreparation(@PathVariable("id") String id, @RequestBody Preparation preparation);

    /**
     * Delete a preparation by id. Delete a preparation content based on provided id. Id should be a UUID returned by the list
     * operation. Not valid or non existing preparation id returns empty content.
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/preparations/{id}", method = DELETE, consumes = MediaType.ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @Timed
    String deletePreparation(@PathVariable("id") String id);

    /**
     * Copy a preparation from the given id
     *
     * @param id the preparation id to copy
     * @param destination where to copy the preparation to.
     * @param newName optional new name for the preparation.
     * @return The copied preparation id.
     */
    @RequestMapping(value = "/api/preparations/{id}/copy", method = POST, produces = TEXT_PLAIN_VALUE)
    String copy(@PathVariable(value = "id") String id, @RequestParam(required = false, name = "newName") String newName,
            @RequestParam(required = false, name = "destination") String destination);

    /**
     * Move a preparation to another folder.
     *
     * @param id the preparation id to move.
     * @param folder where to find the preparation.
     * @param destination where to move the preparation.
     * @param newName optional new preparation name.
     */
    @RequestMapping(value = "/api/preparations/{id}/move", method = PUT, produces = TEXT_PLAIN_VALUE)
    @Timed
    void move(@PathVariable(value = "id") String id,
            @RequestParam(defaultValue = "", required = false, name = "folder") String folder,
            @RequestParam(name = "destination") String destination,
            @RequestParam(defaultValue = "", required = false, name = "newName") String newName) throws IOException;

    /**
     * Get a preparation by id and details.
     *
     * @param preparationId
     * @return Returns the preparation details.
     */
    @RequestMapping(value = "/api/preparations/{id}/details", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<StreamingResponseBody> getPreparation(@PathVariable(value = "id") String preparationId);

    /**
     * Get preparation content by id and at a given version.
     *
     * @param preparationId Preparation id.
     * @param version Version of the preparation (can be 'origin', 'head' or the version id). Defaults to 'head'.
     * @param from Where to get the data from
     * @return Returns the preparation content at version.
     */
    @RequestMapping(value = "/api/preparations/{id}/content", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody getPreparation(@PathVariable(value = "id") String preparationId, //
            @RequestParam(value = "version", defaultValue = "head", name = "version") String version,
            @RequestParam(value = "from", defaultValue = "HEAD", name = "from") ExportParameters.SourceType from);

    /**
     * Adds an action at the end of preparation. Does not return any value, client may expect successful operation based on HTTP
     * status code.
     *
     * @param preparationId Preparation id.
     * @param actionsContainer Action to add at end of the preparation.
     */
    // TODO: this API should take a list of AppendStep.
    @RequestMapping(value = "/api/preparations/{id}/actions", method = POST, produces = APPLICATION_JSON_VALUE)
    @Timed
    void addPreparationAction(@PathVariable(value = "id") String preparationId, @RequestBody AppendStep actionsContainer);

    /**
     * Updates an action in the preparation. Does not return any value, client may expect successful operation based on HTTP
     * status code.
     *
     * @param preparationId Preparation id.
     * @param stepId Step id in the preparation.
     * @param step New content for the action.
     */
    @RequestMapping(value = "/api/preparations/{preparationId}/actions/{stepId}", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    void updatePreparationAction(@PathVariable(value = "preparationId") String preparationId,
            @PathVariable(value = "stepId") String stepId, @RequestBody AppendStep step);

    /**
     * Delete an action in the preparation. Does not return any value, client may expect successful operation based on HTTP status
     * code.
     *
     * @param preparationId Preparation id.
     * @param stepId Step id to delete.
     */
    @RequestMapping(value = "/api/preparations/{id}/actions/{stepId}", method = DELETE, produces = APPLICATION_JSON_VALUE)
    @Timed
    void deletePreparationAction(@PathVariable(value = "id") String preparationId, @PathVariable(value = "stepId") String stepId);

    /**
     * Changes the head of the preparation. Does not return any value, client may expect successful operation based on HTTP status
     * code.
     *
     * @param preparationId Preparation id.
     * @param headId New head step id
     */
    @RequestMapping(value = "/api/preparations/{id}/head/{headId}", method = PUT)
    @Timed
    void setPreparationHead(@PathVariable(value = "id") String preparationId, @PathVariable(value = "headId") String headId);

    /**
     * Mark a preparation as locked by a user. Does not return any value, client may expect successful operation based on HTTP
     * status code.
     *
     * @param preparationId Preparation id.
     */
    @RequestMapping(value = "/api/preparations/{preparationId}/lock", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    void lockPreparation(@PathVariable(value = "preparationId") String preparationId);

    /**
     * Mark a preparation as unlocked by a user. Does not return any value, client may expect successful operation based on HTTP
     * status code.
     *
     * @param preparationId
     */
    @RequestMapping(value = "/api/preparations/{preparationId}/unlock", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Timed
    void unlockPreparation(@PathVariable(value = "preparationId") String preparationId);

    /**
     * Copy the steps from the another preparation to this one.
     * <p>
     * This is only allowed if this preparation has no steps.
     *
     * @param id the preparation id to update.
     * @param from the preparation id to copy the steps from.
     */
    @RequestMapping(value = "/api/preparations/{id}/steps/copy", method = PUT, produces = TEXT_PLAIN_VALUE)
    @Timed
    void copyStepsFrom(@PathVariable("id") String id, @RequestParam(name = "from") String from);

    /**
     * Moves the step of specified id <i>stepId</i> after step of specified id <i>parentId</i> within the specified preparation.
     *
     * @param preparationId the Id of the specified preparation
     * @param stepId the Id of the specified step to move
     * @param parentStepId the Id of the specified step which will become the parent of the step to move
     */
    @RequestMapping(value = "/api/preparations/{preparationId}/steps/{stepId}/order", method = POST, consumes = APPLICATION_JSON_VALUE)
    @Timed
    void moveStep(@PathVariable("preparationId") String preparationId, @PathVariable("stepId") String stepId,
            @RequestParam(name = "parentStepId") String parentStepId);

    /**
     * Get a preview diff between 2 steps of the same preparation.
     *
     * @param input
     * @return
     */
    @RequestMapping(value = "/api/preparations/preview/diff", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody previewDiff(@RequestBody PreviewDiffParameters input);

    /**
     * Get a preview diff between the same step of the same preparation but with one step update.
     *
     * @param input
     * @return
     */
    @RequestMapping(value = "/api/preparations/preview/update", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    StreamingResponseBody previewUpdate(@RequestBody PreviewUpdateParameters input);

    /**
     * Get a preview between the head step and a new appended transformation.
     *
     * @param input
     * @return
     */
    @RequestMapping(value = "/api/preparations/preview/add", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    StreamingResponseBody previewAdd(@RequestBody @Valid PreviewAddParameters input);

    /**
     * Return the semantic types for a given preparation / column.
     *
     * @param preparationId the preparation id.
     * @param columnId the column id.
     * @param stepId the step id (optional, if not specified, it's 'head')
     * @return the semantic types for a given preparation / column.
     */
    @RequestMapping(value = "/api/preparations/{preparationId}/columns/{columnId}/types", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    ResponseEntity<StreamingResponseBody> getPreparationColumnSemanticCategories(
            @PathVariable("preparationId") String preparationId, @PathVariable("columnId") String columnId,
            @RequestParam(defaultValue = "head", name = "stepId") String stepId);
}
