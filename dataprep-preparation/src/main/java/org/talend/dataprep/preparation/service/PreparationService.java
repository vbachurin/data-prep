package org.talend.dataprep.preparation.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.talend.dataprep.api.preparation.*;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.MockErrorCode;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.preparation.api.AppendStep;
import org.talend.dataprep.preparation.exception.PreparationErrorCodes;
import org.talend.dataprep.preparation.store.ContentCache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "preparations", basePath = "/preparations", description = "Operations on preparations")
public class PreparationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreparationService.class);

    @Autowired
    private ContentCache cache;

    @Autowired
    private PreparationRepository preparationRepository = null;

    /**
     * Get user name from Spring Security context
     *
     * @return "anonymous" if no user is currently logged in, the user name otherwise.
     */
    private static String getUserName() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String author;
        if (principal != null) {
            author = principal.toString();
        } else {
            author = "anonymous"; //$NON-NLS-1
        }
        return author;
    }

    private static String getStepId(@ApiParam("version") @PathVariable("version") String version, Preparation preparation) {
        String stepId;
        if ("head".equalsIgnoreCase(version)) { //$NON-NLS-1$
            stepId = preparation.getStep().id();
        } else if ("origin".equalsIgnoreCase(version)) { //$NON-NLS-1$
            stepId = ROOT_STEP.id();
        } else {
            stepId = version;
        }
        return stepId;
    }

    @RequestMapping(value = "/preparations", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations ids the current user is allowed to see. Creation date is always displayed in UTC time zone. See 'preparations/all' to get all details at once.")
    @Timed
    public List<String> list() {
        LOGGER.debug("Get list of preparations (summary).");
        return preparationRepository.listAll(Preparation.class).stream().map(Preparation::id).collect(toList());
    }

    /**
     * Return all preparations for the given dataset.
     *
     * @param dataSetId the dataSet id.
     * @return all preparations for the given dataset.
     */
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
    public String update(@ApiParam("id") @PathVariable("id") String id, @ApiParam("preparation")
    @RequestBody
    final Preparation preparation) {
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

    @RequestMapping(value = "/preparations/{id}/content/{version}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get preparation details", notes = "Return the details of the preparation with provided id.")
    @Timed
    public void get(@ApiParam("id")
    @PathVariable("id")
    final String id, @ApiParam("version")
    @PathVariable("version")
    final String version, final HttpServletResponse response) {
        LOGGER.debug("Get content of preparation #{} at version '{}'.", id, version);
        final Preparation preparation = preparationRepository.get(id, Preparation.class);
        final Step step = preparationRepository.get(getStepId(version, preparation), Step.class);
        LOGGER.debug("Get content of preparation #{} at step: {}", id, step);
        try {
            ServletOutputStream stream = response.getOutputStream();
            if (cache.has(id, step.id())) {
                LOGGER.debug("Cache exists for preparation #{} at step {}", id, step);
                response.setStatus(HttpServletResponse.SC_OK);
                IOUtils.copyLarge(cache.get(id, step.id()), stream);
            } else {
                LOGGER.debug("Cache does NOT exist for preparation #{} at step {}", id, step);
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                IOUtils.copy(new ByteArrayInputStream(new byte[0]), stream);
            }
            stream.flush();
        } catch (IOException e) {
            throw new TDPException(PreparationErrorCodes.UNABLE_TO_SERVE_PREPARATION_CONTENT, e, TDPExceptionContext.build()
                    .put("id", id).put("version", version));
        }
    }

    @RequestMapping(value = "/preparations/{id}/actions", method = POST, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an action to a preparation", notes = "Append an action at end of the preparation with given id.")
    @Timed
    public void append(@PathVariable("id")
    final String id, @RequestBody
    final AppendStep step) {
        LOGGER.debug("Adding actions to preparation #{}", id);
        final Preparation preparation = preparationRepository.get(id, Preparation.class);
        if (preparation == null) {
            LOGGER.error("Preparation #{} does not exist", id);
            throw new TDPException(PreparationErrorCodes.PREPARATION_DOES_NOT_EXIST, TDPExceptionContext.build().put("id", id));
        }
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
        preparation.setStep(newStep);
        preparation.updateLastModificationDate();
        preparationRepository.add(preparation);
        LOGGER.debug("Added head to preparation #{}: head is now {}", id, newStep.id());
    }

    @RequestMapping(value = "/preparations/{id}/actions/{action}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates an action to in a preparation", notes = "Modifies an action in preparation's steps.")
    @Timed
    public void updateAction(@PathVariable("id")
    final String id, @PathVariable("action")
    final String action, @RequestBody
    final AppendStep step) {
        LOGGER.debug("Modifying actions in preparation #{}", id);
        final Preparation preparation = preparationRepository.get(id, Preparation.class);
        if (preparation == null) {
            LOGGER.error("Preparation #{} does not exist", id);
            throw new TDPException(PreparationErrorCodes.PREPARATION_DOES_NOT_EXIST, TDPExceptionContext.build().put("id", id));
        }
        final Step head = preparation.getStep();
        LOGGER.debug("Current head for preparation #{}: {}", id, head);
        // Add update preparation step
        final List<String> steps = PreparationUtils.listSteps(head, action, preparationRepository);
        LOGGER.debug("Rewriting history for {} steps.", steps.size());
        // Build list of actions added at each step
        List<AppendStep> appends = new ArrayList<>(steps.size());
        appends.add(step);
        for (int i = steps.size() - 1; i > 0; i--) {
            final List<Action> previous = getActions(steps.get(i));
            final List<Action> current = getActions(steps.get(i - 1));
            final AppendStep appendStep = new AppendStep();
            appendStep.setActions(current.subList(previous.size(), current.size()));
            appends.add(appendStep);
        }
        // Rebuild history from modified step
        final Step modifiedStep = preparationRepository.get(steps.get(steps.size() - 1), Step.class);
        preparation.setStep(preparationRepository.get(modifiedStep.getParent(), Step.class));
        preparationRepository.add(preparation);
        for (AppendStep append : appends) {
            append(preparation.getId(), append);
        }
        final Step newHead = preparationRepository.get(id, Preparation.class).getStep();
        LOGGER.debug("Modified head of preparation #{}: head is now {}", newHead.getId());
    }

    private List<Action> getActions(String stepId) {
        return new ArrayList<>(preparationRepository.get(preparationRepository.get(stepId, Step.class).getContent(),
                PreparationActions.class).getActions());
    }

    @RequestMapping(value = "/preparations/{id}/actions/{version}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the action on preparation at given version.", notes = "Returns the action JSON at version.")
    @Timed
    public PreparationActions getVersionedAction(@ApiParam("id")
    @PathVariable("id")
    final String id, @ApiParam("version")
    @PathVariable("version")
    final String version) {
        LOGGER.debug("Get list of actions of preparations #{} at version {}.", id, version);
        final Preparation preparation = preparationRepository.get(id, Preparation.class);
        if (preparation != null) {
            final String stepId = getStepId(version, preparation);
            final Step step = preparationRepository.get(stepId, Step.class);
            return preparationRepository.get(step.getContent(), PreparationActions.class);
        } else {
            throw new TDPException(PreparationErrorCodes.PREPARATION_DOES_NOT_EXIST, TDPExceptionContext.build().put("id", id));
        }
    }

    /**
     * List all preparation related error codes.
     */
    @RequestMapping(value = "/preparations/errors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all preparation related error codes.", notes = "Returns the list of all preparation related error codes.")
    @Timed
    public String listErrors() {
        try {
            // need to cast the typed dataset errors into mock ones to use json parsing
            List<MockErrorCode> errors = new ArrayList<>(PreparationErrorCodes.values().length);
            for (PreparationErrorCodes code : PreparationErrorCodes.values()) {
                errors.add(new MockErrorCode(code));
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(errors);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }
}
