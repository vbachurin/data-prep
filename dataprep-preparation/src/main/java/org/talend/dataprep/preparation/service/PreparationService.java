package org.talend.dataprep.preparation.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;
import static org.talend.dataprep.preparation.exception.PreparationErrorCodes.PREPARATION_DOES_NOT_EXIST;
import static org.talend.dataprep.preparation.exception.PreparationErrorCodes.PREPARATION_STEP_DOES_NOT_EXIST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.json.JsonErrorCodeDescription;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.preparation.api.AppendStep;
import org.talend.dataprep.preparation.exception.PreparationErrorCodes;

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

    @RequestMapping(value = "/preparations/{id}/actions", method = POST, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an action to a preparation", notes = "Append an action at end of the preparation with given id.")
    @Timed
    public void append(@PathVariable("id") final String id, //
                       @RequestBody final AppendStep step) {
        LOGGER.debug("Adding actions to preparation #{}", id);
        final Preparation preparation = getPreparation(id);

        final Step head = preparation.getStep();
        LOGGER.debug("Current head for preparation #{}: {}", id, head);

        // Add new actions
        final PreparationActions headContent = preparationRepository.get(head.getContent(), PreparationActions.class);
        final PreparationActions newContent = headContent.append(step.getActions());
        preparationRepository.add(newContent);

        // Create new step from new content
        final Step newStep = new Step(head.id(), newContent.id());
        preparationRepository.add(newStep);

        // Update preparation head step
        setPreparationHead(preparation, newStep);
        LOGGER.debug("Added head to preparation #{}: head is now {}", id, newStep.id());
    }

    @RequestMapping(value = "/preparations/{id}/actions/{action}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates an action in a preparation", notes = "Modifies an action in preparation's steps.")
    @Timed
    public void updateAction(@PathVariable("id") final String id, //
                             @PathVariable("action") final String action, //
                             @RequestBody final AppendStep step) {
        LOGGER.debug("Modifying actions in preparation #{}", id);
        final Preparation preparation = getPreparation(id);

        // Get steps from modified to the head
        final Step head = preparation.getStep();
        LOGGER.debug("Current head for preparation #{}: {}", id, head);
        final List<String> steps = PreparationUtils.listSteps(head, action, preparationRepository);
        LOGGER.debug("Rewriting history for {} steps.", steps.size());

        // Build list of actions from modified one to the head
        List<AppendStep> appends = new ArrayList<>(steps.size());
        appends.add(step);
        for (int i = 1; i < steps.size(); ++i) {
            final List<Action> previous = getActions(steps.get(i - 1));
            final List<Action> current = getActions(steps.get(i));
            final AppendStep appendStep = new AppendStep();
            appendStep.setActions(current.subList(previous.size(), current.size()));
            appends.add(appendStep);
        }

        // Rebuild history from modified step : needed because the ids will be regenerated at each step
        final Step modifiedStep = preparationRepository.get(steps.get(0), Step.class);
        final Step previousStep = preparationRepository.get(modifiedStep.getParent(), Step.class);
        setPreparationHead(preparation, previousStep);
        for (AppendStep append : appends) {
            append(preparation.getId(), append);
        }
        final Step newHead = preparationRepository.get(id, Preparation.class).getStep();
        LOGGER.debug("Modified head of preparation #{}: head is now {}", newHead.getId());

        //TODO JSO : what to do with the old steps that are deleted but still in repository ?
    }

    @RequestMapping(value = "/preparations/{id}/actions/{action}", method = DELETE)
    @ApiOperation(value = "Delete an action in a preparation", notes = "Delete a step and all following steps from a preparation")
    @Timed
    public void deleteAction(@PathVariable("id") final String id, //
                             @PathVariable("action") final String action) throws TDPException {
        LOGGER.debug("Deleting actions in preparation #{}", id);
        final Preparation preparation = getPreparation(id);

        // Get all steps
        final Step head = preparation.getStep();
        LOGGER.debug("Current head for preparation #{}: {}", id, head);
        final List<String> steps = PreparationUtils.listSteps(head, null, preparationRepository);
        if (!steps.contains(action)) {
            throw new TDPException(PREPARATION_STEP_DOES_NOT_EXIST,
                    TDPExceptionContext.build()
                            .put("id", id)
                            .put("stepId", action));
        }

        // Replace head by the step before the deleted one
        final Step stepToDelete = preparationRepository.get(action, Step.class);
        final Step newHead = preparationRepository.get(stepToDelete.getParent(), Step.class);
        setPreparationHead(preparation, newHead);

        //TODO JSO : what to do with the deleted step and the following steps that are deleted but still in repository ?
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

    /**
     * Get user name from Spring Security context
     *
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
     * @param version The version to convert to step id
     * @param preparation The preparation
     * @return The converted step Id
     */
    private static String getStepId(final String version, final Preparation preparation) {
        if ("head".equalsIgnoreCase(version)) { //$NON-NLS-1$
            return preparation.getStep().id();
        }
        else if ("origin".equalsIgnoreCase(version)) { //$NON-NLS-1$
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
     *
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
     * Update the head step of a preparation
     *
     * @param preparation The preparation to update
     * @param head        The head step
     */
    private void setPreparationHead(final Preparation preparation, final Step head) {
        preparation.setStep(head);
        preparation.updateLastModificationDate();
        preparationRepository.add(preparation);
    }
}
