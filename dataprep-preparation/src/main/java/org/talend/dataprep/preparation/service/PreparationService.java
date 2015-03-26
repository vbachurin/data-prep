package org.talend.dataprep.preparation.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.talend.dataprep.preparation.Step.ROOT_STEP;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.preparation.Preparation;
import org.talend.dataprep.preparation.PreparationActions;
import org.talend.dataprep.preparation.PreparationRepository;
import org.talend.dataprep.preparation.Step;
import org.talend.dataprep.preparation.api.AppendStep;
import org.talend.dataprep.preparation.store.ContentCache;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@Api(value = "preparations", basePath = "/preparations", description = "Operations on preparations")
public class PreparationService {

    private static final Log LOGGER = LogFactory.getLog(PreparationService.class);

    @Autowired
    private ContentCache cache;

    @Autowired
    private PreparationRepository versionRepository = null;

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

    private static String getStepId(@ApiParam(value = "version") @PathVariable(value = "version") String version,
            Preparation preparation) {
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
        return versionRepository.listAll(Preparation.class).stream().map(Preparation::id).collect(toList());
    }

    @RequestMapping(value = "/preparations/all", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List all preparations", notes = "Returns the list of preparations the current user is allowed to see. Creation date is always displayed in UTC time zone. This operation return all details on the preparations.")
    @Timed
    public Set<Preparation> listAll() {
        return versionRepository.listAll(Preparation.class);
    }

    @RequestMapping(value = "/preparations", method = PUT, produces = TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a preparation", notes = "Returns the id of the created preparation.")
    @Timed
    public String create(@ApiParam(value = "datasetId") @RequestBody final String dataSetId) {
        if (org.apache.commons.lang.StringUtils.isBlank(dataSetId)) {
            throw new IllegalArgumentException("Unable to create preparation, dataset id is blank");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create new preparation for data set " + dataSetId);
        }

        final Preparation preparation = new Preparation(dataSetId, ROOT_STEP);
        preparation.setAuthor(getUserName());
        versionRepository.add(preparation);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created new preparation: " + preparation);
        }
        return preparation.id();
    }

    @RequestMapping(value = "/preparations/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get preparation details", notes = "Return the details of the preparation with provided id.")
    @Timed
    public Preparation get(@ApiParam(value = "id") @PathVariable(value = "id") String id) {
        return versionRepository.get(id, Preparation.class);
    }

    @RequestMapping(value = "/preparations/{id}/content/{version}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get preparation details", notes = "Return the details of the preparation with provided id.")
    @Timed
    public void get(@ApiParam(value = "id") @PathVariable(value = "id") final String id,
                    @ApiParam(value = "version") @PathVariable(value = "version") final String version,
                    final HttpServletResponse response) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get content of preparation #" + id + " at version '" + version + "'.");
        }

        final Preparation preparation = versionRepository.get(id, Preparation.class);
        final Step step = versionRepository.get(getStepId(version, preparation), Step.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get content of preparation #" + id + " at step: " + step);
        }

        try {
            if (cache.has(id, step.id())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cache exists for preparation #" + id + " at step " + step);
                }
                ServletOutputStream stream = response.getOutputStream();
                response.setStatus(HttpServletResponse.SC_OK);
                IOUtils.copyLarge(cache.get(id, step.id()), stream);
                stream.flush();
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cache does NOT exist for preparation #" + id + " at step " + step);
                }
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to serve content at version #" + version + " for preparation #" + id, e);
        }
    }

    @RequestMapping(value = "/preparations/{id}/actions", method = POST, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an action to a preparation", notes = "Append an action at end of the preparation with given id.")
    @Timed
    public void append(@PathVariable(value = "id") final String id, @RequestBody final AppendStep step) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding actions to preparation #" + id);
        }

        final Preparation preparation = versionRepository.get(id, Preparation.class);
        if (preparation == null) {
            LOGGER.error("Preparation #" + id + " does not exist");
            throw new RuntimeException("Preparation id #" + id + " does not exist.");
        }

        final Step head = preparation.getStep();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Current head for preparation #" + id + ": " + head);
        }

        // Add new actions
        final PreparationActions headContent = versionRepository.get(head.getContent(), PreparationActions.class);
        final PreparationActions newContent = headContent.append(step.getActions());
        versionRepository.add(newContent);

        // Create new step from new content
        final Step newStep = new Step(head.id(), newContent.id());
        versionRepository.add(newStep);

        // Update preparation head step
        preparation.setStep(newStep);
        versionRepository.add(preparation);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Added head to preparation #" + id + ": head is now " + newStep.id());
        }
    }

    @RequestMapping(value = "/preparations/{id}/actions/{version}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the action on preparation at given version.", notes = "Returns the action JSON at version.")
    @Timed
    public PreparationActions getVersionedAction(@ApiParam(value = "id")
    @PathVariable(value = "id")
    final String id, @ApiParam(value = "version")
    @PathVariable(value = "version")
    final String version) {
        final Preparation preparation = versionRepository.get(id, Preparation.class);
        if (preparation != null) {
            final String stepId = getStepId(version, preparation);
            final Step step = versionRepository.get(stepId, Step.class);
            return versionRepository.get(step.getContent(), PreparationActions.class);
        } else {
            throw new RuntimeException("Preparation id #" + id + " does not exist.");
        }
    }
}
