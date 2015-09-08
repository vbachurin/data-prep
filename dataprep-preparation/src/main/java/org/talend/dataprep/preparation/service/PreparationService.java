package org.talend.dataprep.preparation.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;
import static org.talend.dataprep.preparation.exception.PreparationErrorCodes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.preparation.*;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.preparation.api.AppendStep;
import org.talend.dataprep.preparation.exception.PreparationErrorCodes;
import org.talend.dataprep.preparation.store.PreparationRepository;
import org.talend.dataprep.transformation.api.action.validation.ActionMetadataValidation;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "preparations", basePath = "/preparations", description = "Operations on preparations")
public class PreparationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreparationService.class);

    @Autowired
    private PreparationRepository preparationRepository = null;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private ActionMetadataValidation validator;

    @RequestMapping(value = "/preparations", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations ids the current user is allowed to see. Creation date is always displayed in UTC time zone. See 'preparations/all' to get all details at once.")
    @Timed
    public List<String> list() {
        LOGGER.debug("Get list of preparations (summary).");
        return preparationRepository.listAll(Preparation.class).stream().map(Preparation::id).collect(toList());
    }

    @RequestMapping(value = "/preparations", method = GET, params = "dataSetId", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations for the given DataSet id", notes = "Returns the list of preparations for the given Dataset id the current user is allowed to see. Creation date is always displayed in UTC time zone. See 'preparations/all' to get all details at once.")
    @Timed
    public Collection<Preparation> listByDataSet(@RequestParam("dataSetId") @ApiParam("dataSetId") String dataSetId) {
        Collection<Preparation> preparations = preparationRepository.getByDataSet(dataSetId);
        LOGGER.debug("{} preparation(s) use dataset {}.", preparations.size(), dataSetId);
        return preparations;
    }

    @RequestMapping(value = "/preparations/all", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations the current user is allowed to see. Creation date is always displayed in UTC time zone. This operation return all details on the preparations.")
    @Timed
    public Collection<Preparation> listAll() {
        LOGGER.debug("Get list of preparations (with details).");
        return preparationRepository.listAll(Preparation.class);
    }

    @RequestMapping(value = "/preparations", method = PUT, produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a preparation", notes = "Returns the id of the created preparation.")
    @Timed
    public String create(@ApiParam("preparation")
                         @RequestBody
                         final Preparation preparation) {
        LOGGER.debug("Create new preparation for data set {}", preparation.getDataSetId());
        preparation.setStep(ROOT_STEP);
        preparation.setAuthor(getUserName());
        preparationRepository.add(preparation);
        LOGGER.debug("Created new preparation: {}", preparation);
        return preparation.id();
    }

    @RequestMapping(value = "/preparations/{id}", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Delete a preparation by id", notes = "Delete a preparation content based on provided id. Id should be a UUID returned by the list operation. Not valid or non existing preparation id returns empty content.")
    @Timed
    public void delete(@PathVariable(value = "id") @ApiParam(name = "id", value = "Id of the preparation to delete") String id) {
        LOGGER.debug("Deletion of preparation #{} requested.", id);
        Preparation preparationToDelete = preparationRepository.get(id, Preparation.class);
        preparationRepository.remove(preparationToDelete);
        LOGGER.debug("Deletion of preparation #{} done.", id);
    }

    @RequestMapping(value = "/preparations/{id}", method = PUT, produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a preparation", notes = "Returns the id of the updated preparation.")
    @Timed
    public String update(@ApiParam("id") @PathVariable("id") String id, //
                         @ApiParam("preparation") @RequestBody final Preparation preparation) {
        Preparation previousPreparation = preparationRepository.get(id, Preparation.class);
        LOGGER.debug("Updating preparation with id {}: {}", preparation.id(), previousPreparation);
        Preparation updated = previousPreparation.merge(preparation);
        if (!updated.id().equals(id)) {
            preparationRepository.remove(previousPreparation);
        }
        preparationRepository.add(updated);
        LOGGER.debug("Updated preparation: {}", updated);
        return updated.id();
    }

    @RequestMapping(value = "/preparations/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get preparation details", notes = "Return the details of the preparation with provided id.")
    @Timed
    public Preparation get(@ApiParam("id") @PathVariable("id") String id) {
        LOGGER.debug("Get content of preparation details for #{}.", id);
        return preparationRepository.get(id, Preparation.class);
    }

    @RequestMapping(value = "/preparations/{id}/steps", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all preparation steps id", notes = "Return the steps of the preparation with provided id.")
    @Timed
    public List<String> getSteps(@ApiParam("id") @PathVariable("id") String id) {
        LOGGER.debug("Get steps of preparation for #{}.", id);
        final Step step = preparationRepository.get(id, Preparation.class).getStep();
        return PreparationUtils.listSteps(step, preparationRepository);
    }

    /**
     * Append step(s) in a preparation. There is 2 modes : insert after head or insert after a specific step.<br/><br/>
     * <b>After head Strategy</b><br/>
     * Just append the new actions after the current preparation head. One step is created by action.
     *
     * <br/><br/>
     *
     * <b>After a non head step Strategy</b><br/>
     * The goal here is to rewrite the preparation history from the insertion step to the head, with the appended actions.
     * <ul>
     *     <li>1. Extract the actions from insertion step (excluded) to the head</li>
     *     <li>2. Insert the new actions before the other extracted actions</li>
     *     <li>3. Set preparation head to insertion step</li>
     *     <li>4. Append each action (one step is created by action) after the new preparation head</li>
     * </ul>
     */
    @RequestMapping(value = "/preparations/{id}/actions", method = POST, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an action to a preparation", notes = "Append an action at end of the preparation with given id.")
    @Timed
    public void appendSteps(@PathVariable("id") final String id, //
                       @RequestBody final AppendStep stepsToAppend) {
        checkActionStepConsistency(stepsToAppend);

        final String insertionStepId = stepsToAppend.getInsertionStepId();
        LOGGER.debug("Adding actions to preparation #{} after step #{}", id, (insertionStepId == null ? "head" : insertionStepId));

        final Preparation preparation = getPreparation(id);
        LOGGER.debug("Current head for preparation #{}: {}", id, preparation.getStep());

        List<AppendStep> actionsSteps;

        //try to insert step after a specific one (insertionStepId)
        if(! isPreparationHead(preparation, insertionStepId)) {
            //get steps from insertion point to head
            final List<String> steps = extractSteps(preparation, insertionStepId); // throws an exception if stepId is not in the preparation
            LOGGER.debug("Rewriting history for {} steps.", steps.size());

            // before : ... - (insertion point) - (step after insertion point) - ...
            // extract actions AFTER insertion point and add the steps to append before
            // after : ... - (insertion point) - (steps to append) - (step after insertion point) - ...
            actionsSteps = extractActionsAfterStep(steps, insertionStepId);
            actionsSteps.add(0, stepsToAppend);
        }
        //insert after current head
        else {
            actionsSteps = new ArrayList<>(1);
            actionsSteps.add(stepsToAppend);
        }

        //rebuild history from insertion point (or head)
        replaceHistory(preparation, insertionStepId, actionsSteps);
        LOGGER.debug("Added head to preparation #{}: head is now {}", id, preparation.getStep().id());
    }

    /**
     * Update a step in a preparation
     * <b>Strategy</b><br/>
     * The goal here is to rewrite the preparation history from 'the step to modify' (STM) to the head, with STM containing the new action.<br/>
     * <ul>
     *     <li>1. Extract the actions from STM (excluded) to the head</li>
     *     <li>2. Insert the new actions before the other extracted actions. The actions list contains all the actions from the <b>NEW</b> STM to the head</li>
     *     <li>3. Set preparation head to STM's parent, so STM will be excluded</li>
     *     <li>4. Append each action (one step is created by action) after the new preparation head</li>
     * </ul>
     */
    @RequestMapping(value = "/preparations/{id}/actions/{stepId}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates an action in a preparation", notes = "Modifies an action in preparation's steps.")
    @Timed
    public void updateAction(@PathVariable("id") final String id, //
                             @PathVariable("stepId") final String stepToModifyId, //
                             @RequestBody final AppendStep newStep) {
        checkActionStepConsistency(newStep);

        LOGGER.debug("Modifying actions in preparation #{}", id);
        final Preparation preparation = getPreparation(id);
        LOGGER.debug("Current head for preparation #{}: {}", id, preparation.getStep());

        // Get steps from "step to modify" to the head
        final List<String> steps = extractSteps(preparation, stepToModifyId); // throws an exception if stepId is not in the preparation
        LOGGER.debug("Rewriting history for {} steps.", steps.size());

        // Build list of actions from modified one to the head
        final List<AppendStep> actionsSteps = extractActionsAfterStep(steps, stepToModifyId);
        actionsSteps.add(0, newStep);

        // Rebuild history from modified step
        final Step stepToModify = preparationRepository.get(stepToModifyId, Step.class);
        replaceHistory(preparation, stepToModify.getParent(), actionsSteps);
        LOGGER.debug("Modified head of preparation #{}: head is now {}", preparation.getStep().getId());
    }

    /**
     * Delete a step in a preparation. 2 modes : single or cascade.<br/>
     * STD : Step To Delete
     * <br/><br/>
     *
     * <b>Cascade Strategy</b><br/>
     * This mode is easy, we just replace the preparation head to STD's parent.
     *
     * <b>Single Strategy</b><br/>
     * The goal here is to rewrite the preparation history from 'the step to delete' (STD) to the head, STD excluded<br/>
     * <ul>
     *     <li>1. Extract the actions from STD (excluded) to the head. The actions list contains all the actions from the STD's child to the head.</li>
     *     <li>2. Set preparation head to STD's parent, so STD will be excluded</li>
     *     <li>3. Append each action (one step is created by action) after the new preparation head</li>
     * </ul>
     */
    @RequestMapping(value = "/preparations/{id}/actions/{stepId}", method = DELETE)
    @ApiOperation(value = "Delete an action in a preparation", notes = "Delete a step and all following steps from a preparation")
    @Timed
    public void deleteAction(@PathVariable("id") final String id, //
                             @PathVariable("stepId") final String stepToDeleteId,
                             @RequestParam(value = "single", defaultValue = "false") final boolean single) throws TDPException {
        if (ROOT_STEP.getId().equals(stepToDeleteId)) {
            throw new TDPException(PREPARATION_ROOT_STEP_CANNOT_BE_DELETED);
        }

        // get steps from 'step to delete' to head
        final Preparation preparation = getPreparation(id);
        final List<String> steps = extractSteps(preparation, stepToDeleteId); // throws an exception if stepId is not in the preparation

        LOGGER.debug("Deleting actions in preparation #{} at step #{} with single mode '{}'", id, stepToDeleteId, single);

        // single mode : delete a single step
        if(single) {
            final List<AppendStep> actions = extractActionsAfterStep(steps, stepToDeleteId);

            // check that no step after StepId uses the same column
            final List<Action> stepToDeleteActions = getActions(stepToDeleteId);
            final String stepToDeleteColumnId = stepToDeleteActions.get(stepToDeleteActions.size() - 1).getParameters().get("column_id");

            //TODO JSO : pb when action to delete create a column how can we test if an action transform the new created column ?
            if(actionsTransformColumn(actions, stepToDeleteColumnId)) {
                throw new TDPException(PREPARATION_STEP_CANNOT_BE_DELETED_IN_SINGLE_MODE,
                        TDPExceptionContext.build()
                                .put("id", id)
                                .put("stepId", stepToDeleteId));
            }
            final Step stepToDelete = preparationRepository.get(stepToDeleteId, Step.class);
            replaceHistory(preparation, stepToDelete.getParent(), actions);
        }
        // cascade mode : delete cascading from step
        else {
            // Replace head by the step before the deleted one
            final Step stepToDelete = preparationRepository.get(stepToDeleteId, Step.class);
            final Step newHead = preparationRepository.get(stepToDelete.getParent(), Step.class);
            setPreparationHead(preparation, newHead);
        }
    }

    @RequestMapping(value = "/preparations/{id}/actions/{version}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all the actions of a preparation at given version.", notes = "Returns the action JSON at version.")
    @Timed
    public List<Action> getVersionedAction(@ApiParam("id") @PathVariable("id") final String id, //
                                           @ApiParam("version") @PathVariable("version") final String version) {
        LOGGER.debug("Get list of actions of preparations #{} at version {}.", id, version);
        final Preparation preparation = preparationRepository.get(id, Preparation.class);
        if (preparation != null) {
            final String stepId = getStepId(version, preparation);
            final Step step = preparationRepository.get(stepId, Step.class);
            return preparationRepository.get(step.getContent(), PreparationActions.class).getActions();
        } else {
            throw new TDPException(PREPARATION_DOES_NOT_EXIST, TDPExceptionContext.build().put("id", id));
        }
    }

    /**
     * List all preparation related error codes.
     */
    @RequestMapping(value = "/preparations/errors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all preparation related error codes.", notes = "Returns the list of all preparation related error codes.")
    @Timed
    public void listErrors(HttpServletResponse response) {
        try {
            // need to cast the typed dataset errors into mock ones to use json parsing
            List<JsonErrorCodeDescription> errors = new ArrayList<>(PreparationErrorCodes.values().length);
            for (PreparationErrorCodes code : PreparationErrorCodes.values()) {
                errors.add(new JsonErrorCodeDescription(code));
            }
            builder.build().writer().writeValue(response.getOutputStream(), errors);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------GETTERS/EXTRACTORS------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Get user name from Spring Security context
     * @return "anonymous" if no user is currently logged in, the user name otherwise.
     */
    private static String getUserName() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null) {
            return principal.toString();
        }
        return "anonymous"; //$NON-NLS-1
    }

    /**
     * Get the actual step id by converting "head" and "origin" to the hash
     * @param version     The version to convert to step id
     * @param preparation The preparation
     * @return The converted step Id
     */
    private static String getStepId(final String version, final Preparation preparation) {
        if ("head".equalsIgnoreCase(version)) { //$NON-NLS-1$
            return preparation.getStep().id();
        } else if ("origin".equalsIgnoreCase(version)) { //$NON-NLS-1$
            return ROOT_STEP.id();
        }
        return version;
    }

    /**
     * Get actions list from root to the provided step
     * @param stepId The limit step id
     * @return The list of actions
     */
    private List<Action> getActions(final String stepId) {
        return new ArrayList<>(preparationRepository.get(preparationRepository.get(stepId, Step.class).getContent(),
                PreparationActions.class).getActions());
    }

    /**
     * Get preparation from id
     * @param id The preparation id.
     * @return The preparation with the provided id
     * @throws TDPException when no preparation has the provided id
     */
    private Preparation getPreparation(final String id) {
        final Preparation preparation = preparationRepository.get(id, Preparation.class);
        if (preparation == null) {
            LOGGER.error("Preparation #{} does not exist", id);
            throw new TDPException(PREPARATION_DOES_NOT_EXIST, TDPExceptionContext.build().put("id", id));
        }
        return preparation;
    }

    /**
     * Extract all actions after a provided step
     * @param steps The steps list
     * @param afterStep The (excluded) step id where to start the extraction
     * @return The actions after 'afterStep' to the end of the list
     */
    private List<AppendStep> extractActionsAfterStep(final List<String> steps, final String afterStep) {
        final int stepIndex = steps.indexOf(afterStep);
        if(stepIndex == -1) {
            return Collections.emptyList();
        }

        final List<AppendStep> actions = new ArrayList<>(steps.size());
        for (int i = stepIndex + 1; i < steps.size(); ++i) {
            final List<Action> previous = getActions(steps.get(i - 1));
            final List<Action> current = getActions(steps.get(i));
            final AppendStep appendStep = new AppendStep();
            appendStep.setActions(current.subList(previous.size(), current.size()));
            actions.add(appendStep);
        }
        return actions;
    }

    /**
     * Get the steps ids from a specific step to the head. The specific step MUST be defined as an existing step of the preparation
     * @param preparation The preparation
     * @param fromStepId The starting step id
     * @return The steps ids from 'fromStepId' to the head
     * @throws TDPException If 'fromStepId' is not a step of the provided preparation
     */
    private List<String> extractSteps(final Preparation preparation, final String fromStepId) {
        final List<String> steps = PreparationUtils.listSteps(preparation.getStep(), fromStepId, preparationRepository);
        if(!fromStepId.equals(steps.get(0))) {
            throw new TDPException(PREPARATION_STEP_DOES_NOT_EXIST,
                    TDPExceptionContext.build()
                            .put("id", preparation.getId())
                            .put("stepId", fromStepId));
        }
        return steps;
    }

    //------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------CHECKERS-----------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Test if the stepId is the preparation head.
     * Null, "head", "origin" and the actual step id are considered to be the head
     * @param preparation The preparation to test
     * @param stepId The step id to test
     * @return True if 'stepId' is considered as the preparation head
     */
    private boolean isPreparationHead(final Preparation preparation, final String stepId) {
        return stepId == null || "head".equals(stepId) || "origin".equals(stepId) || preparation.getStep().getId().equals(stepId);
    }

    /**
     * Check if the action list affect a column
     * @param actions The list of actions
     * @param columnId The columnId th check
     * @return True if one of the actions affect the column
     */
    private boolean actionsTransformColumn(final List<AppendStep> actions, final String columnId) {
        for(final AppendStep nextStep: actions) {
            final String nextStepColumnId = nextStep.getActions().get(0).getParameters().get("column_id");
            if(columnId.equals(nextStepColumnId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check the action parameters consistency
     * @param step the step to check
     */
    private void checkActionStepConsistency(final AppendStep step) {
        for (final Action stepAction : step.getActions()) {
            validator.checkScopeConsistency(stepAction.getAction(), stepAction.getParameters());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------HISTORY------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Update the head step of a preparation
     * @param preparation The preparation to update
     * @param head        The head step
     */
    private void setPreparationHead(final Preparation preparation, final Step head) {
        preparation.setStep(head);
        preparation.updateLastModificationDate();
        preparationRepository.add(preparation);
    }

    /**
     * Rewrite the preparation history from a specific step, with the provided actions
     * @param preparation The preparation
     * @param startStepId The step id to start the (re)write. The following steps will be erased
     * @param actionsSteps The actions to perform
     */
    private void replaceHistory(final Preparation preparation, final String startStepId, final List<AppendStep> actionsSteps) {
        //move preparation head to the starting step
        if(! isPreparationHead(preparation, startStepId)) {
            final Step startingStep = preparationRepository.get(startStepId, Step.class);
            setPreparationHead(preparation, startingStep);
        }

        //extract steps actions
        final List<Action> actions = actionsSteps
                .stream()
                .flatMap((step) -> step.getActions().stream())
                .collect(toList());

        //append each action to the current preparation head
        appendMultipleStepsToHead(preparation, actions);
    }

    /**
     * Append Multiple steps (1 per action) after the preparation head
     * @param preparation The preparation
     * @param actions The actions list to apply
     */
    private void appendMultipleStepsToHead(final Preparation preparation, final List<Action> actions) {
        for(final Action action : actions) {
            appendSingleStepToHead(preparation, action);
        }
    }

    /**
     * Append a single step (a single action) after the preparation head
     * @param preparation The preparation
     * @param action The action to apply
     */
    private void appendSingleStepToHead(final Preparation preparation, final Action action) {
        final List<Action> actions = new ArrayList<>(1);
        actions.add(action);
        appendSingleStepToHead(preparation, actions);
    }

    /**
     * Append a single step after the preparation head
     * @param preparation The preparation
     * @param actions The actions to apply as 1 step
     */
    private void appendSingleStepToHead(final Preparation preparation, final List<Action> actions) {
        // Add new actions after head
        final Step head = preparation.getStep();
        final PreparationActions headContent = preparationRepository.get(head.getContent(), PreparationActions.class);
        final PreparationActions newContent = headContent.append(actions);
        preparationRepository.add(newContent);

        // Create new step from new content
        final Step newStep = new Step(head.id(), newContent.id());
        preparationRepository.add(newStep);

        // Update preparation head step
        setPreparationHead(preparation, newStep);
    }
}
