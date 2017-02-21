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

package org.talend.dataprep.api.service;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.talend.daikon.exception.ExceptionContext.withBuilder;
import static org.talend.dataprep.exception.error.APIErrorCodes.INVALID_HEAD_STEP_USING_DELETED_DATASET;
import static org.talend.dataprep.exception.error.PreparationErrorCodes.PREPARATION_STEP_DOES_NOT_EXIST;
import static org.talend.dataprep.exception.error.PreparationErrorCodes.UNABLE_TO_READ_PREPARATION;
import static org.talend.dataprep.util.SortAndOrderHelper.Order;
import static org.talend.dataprep.util.SortAndOrderHelper.Sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Client;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.api.service.api.PreviewAddParameters;
import org.talend.dataprep.api.service.api.PreviewDiffParameters;
import org.talend.dataprep.api.service.api.PreviewUpdateParameters;
import org.talend.dataprep.api.service.command.dataset.CompatibleDataSetList;
import org.talend.dataprep.api.service.command.preparation.*;
import org.talend.dataprep.api.service.command.transformation.GetPreparationColumnTypes;
import org.talend.dataprep.command.CommandHelper;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.command.dataset.DataSetGetMetadata;
import org.talend.dataprep.command.preparation.PreparationDetailsGet;
import org.talend.dataprep.command.preparation.PreparationGetActions;
import org.talend.dataprep.command.preparation.PreparationUpdate;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.transformation.actions.datablending.Lookup;
import org.talend.dataprep.util.SortAndOrderHelper;
import org.talend.services.dataprep.DataSetService;
import org.talend.services.dataprep.api.PreparationAPI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.netflix.hystrix.HystrixCommand;

@ServiceImplementation
public class PreparationAPIImpl extends APIService implements PreparationAPI {

    @Client
    private DataSetService dataSetService;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        // This allow to bind Sort and Order parameters in lower-case even if the key is uppercase.
        // URLs are cleaner in lowercase.
        binder.registerCustomEditor(Sort.class, SortAndOrderHelper.getSortPropertyEditor());
        binder.registerCustomEditor(Order.class, SortAndOrderHelper.getOrderPropertyEditor());
    }

    @Override
    public ResponseEntity<StreamingResponseBody> listPreparations(String format, Sort sort, Order order) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing preparations (pool: {} )...", getConnectionStats());
        }
        PreparationList.Format listFormat = PreparationList.Format.valueOf(format.toUpperCase());
        GenericCommand<InputStream> command = getCommand(PreparationList.class, listFormat, sort, order);
        return CommandHelper.toStreaming(command);
    }

    @Override
    public StreamingResponseBody listCompatibleDatasets(String preparationId, Sort sort, Order order) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing compatible datasets (pool: {} )...", getConnectionStats());
        }

        try {
            // get the preparation
            final Preparation preparation = internalGetPreparation(preparationId);

            // to list compatible datasets
            String dataSetId = preparation.getDataSetId();
            HystrixCommand<InputStream> listCommand = getCommand(CompatibleDataSetList.class, dataSetId, sort, order);
            return CommandHelper.toStreaming(listCommand);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Listing compatible datasets (pool: {}) done.", getConnectionStats());
            }
        }
    }

    @Override
    public String createPreparation(String folder, Preparation preparation) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating a preparation in {} (pool: {} )...", folder, getConnectionStats());
        }

        DataSetGetMetadata dataSetMetadata = getCommand(DataSetGetMetadata.class, preparation.getDataSetId());
        DataSetMetadata execute = dataSetMetadata.execute();
        preparation.setRowMetadata(execute.getRowMetadata());

        PreparationCreate preparationCreate = getCommand(PreparationCreate.class, preparation, folder);
        final String preparationId = preparationCreate.execute();

        LOG.info("new preparation {} created in {}", preparationId, folder);

        return preparationId;
    }

    @Override
    public String updatePreparation(String id, Preparation preparation) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating preparation (pool: {} )...", getConnectionStats());
        }
        PreparationUpdate preparationUpdate = getCommand(PreparationUpdate.class, id, preparation);
        final String preparationId = preparationUpdate.execute();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated preparation (pool: {} )...", getConnectionStats());
        }
        return preparationId;
    }

    @Override
    public String deletePreparation(String id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Deleting preparation (pool: {} )...", getConnectionStats());
        }
        final CachePreparationEviction evictPreparationCache = getCommand(CachePreparationEviction.class, id);
        final PreparationDelete preparationDelete = getCommand(PreparationDelete.class, id, evictPreparationCache);
        final String preparationId = preparationDelete.execute();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Deleted preparation (pool: {} )...", getConnectionStats());
        }
        return preparationId;
    }

    @Override
    public String copy(String id, String newName, String destination) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Copying preparation {} to '{}' with new name '{}' (pool: {} )...", id, destination, newName,
                    getConnectionStats());
        }

        HystrixCommand<String> copy = getCommand(PreparationCopy.class, id, destination, newName);
        String copyId = copy.execute();

        LOG.info("Preparation {} copied to {}/{} done --> {}", id, destination, newName, copyId);

        return copyId;
    }

    @Override
    public void move(String id, String folder, String destination, String newName) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Moving preparation (pool: {} )...", getConnectionStats());
        }

        HystrixCommand<Void> move = getCommand(PreparationMove.class, id, folder, destination, newName);
        move.execute();

        LOG.info("Preparation {} moved from {} to {}/'{}'", id, folder, destination, newName);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getPreparation(String preparationId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving preparation details (pool: {} )...", getConnectionStats());
        }

        final EnrichedPreparationDetails enrichPreparation = getCommand(EnrichedPreparationDetails.class, preparationId);
        try {
            return CommandHelper.toStreaming(enrichPreparation);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieved preparation details (pool: {} )...", getConnectionStats());
            }
            LOG.info("Preparation {} retrieved", preparationId);
        }
    }

    @Override
    public StreamingResponseBody getPreparation(String preparationId, String version, ExportParameters.SourceType from) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving preparation content for {}/{} (pool: {} )...", preparationId, version, getConnectionStats());
        }

        try {
            HystrixCommand<InputStream> command = getCommand(PreparationGetContent.class, preparationId, version, from);
            return CommandHelper.toStreaming(command);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieved preparation content (pool: {} )...", getConnectionStats());
            }
        }
    }

    // TODO: this API should take a list of AppendStep.
    @Override
    public void addPreparationAction(String preparationId, AppendStep actionsContainer) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding action to preparation (pool: {} )...", getConnectionStats());
        }

        // This trick is to keep the API taking and unrolling ONE AppendStep until the codefreeze but this must not stay that way
        List<AppendStep> stepsToAppend = actionsContainer.getActions().stream().map(a -> {
            AppendStep s = new AppendStep();
            s.setActions(singletonList(a));
            return s;
        }).collect(toList());

        getCommand(PreparationAddAction.class, preparationId, stepsToAppend).execute();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Added action to preparation (pool: {} )...", getConnectionStats());
        }

    }

    @Override
    public void updatePreparationAction(String preparationId, String stepId, AppendStep step) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating preparation action at step #{} (pool: {} )...", stepId, getConnectionStats());
        }

        // get the preparation
        Preparation preparation = internalGetPreparation(preparationId);

        // get the preparation actions for up to the updated action
        final int stepIndex = preparation.getSteps().stream().map(Step::getId).collect(toList()).indexOf(stepId);
        final String parentStepId = preparation.getSteps().get(stepIndex - 1).id();
        final PreparationGetActions getActionsCommand = getCommand(PreparationGetActions.class, preparationId, parentStepId);

        // get the diff
        final DiffMetadata diffCommand = getCommand(DiffMetadata.class, preparation.getDataSetId(), preparationId,
                step.getActions(), getActionsCommand);

        // get the update action command and execute it
        final HystrixCommand<Void> command = getCommand(PreparationUpdateAction.class, preparationId, stepId, step, diffCommand);
        command.execute();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated preparation action at step #{} (pool: {} )...", stepId, getConnectionStats());
        }
    }

    @Override
    public void deletePreparationAction(String preparationId, String stepId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Deleting preparation action at step #{} (pool: {} ) ...", stepId, //
                    getConnectionStats());
        }

        final HystrixCommand<Void> command = getCommand(PreparationDeleteAction.class, preparationId, stepId);
        command.execute();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Deleted preparation action at step #{} (pool: {} ) ...", stepId, //
                    getConnectionStats());
        }
    }

    @Override
    public void setPreparationHead(String preparationId, String headId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Moving preparation #{} head to step '{}'...", preparationId, headId);
        }

        Step step = getCommand(FindStep.class, headId).execute();
        if (step == null) {
            throw new TDPException(PREPARATION_STEP_DOES_NOT_EXIST);
        } else if (isHeadStepDependingOnDeletedDataSet(step)) {
            final HystrixCommand<Void> command = getCommand(PreparationMoveHead.class, preparationId, headId);
            command.execute();
        } else {
            throw new TDPException(INVALID_HEAD_STEP_USING_DELETED_DATASET);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Moved preparation #{} head to step '{}'...", preparationId, headId);
        }
    }

    @Override
    public void lockPreparation(String preparationId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Locking preparation #{}...", preparationId);
        }

        final HystrixCommand<Void> command = getCommand(PreparationLock.class, preparationId);
        command.execute();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Locked preparation #{}...", preparationId);
        }
    }

    @Override
    public void unlockPreparation(String preparationId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Locking preparation #{}...", preparationId);
        }

        final HystrixCommand<Void> command = getCommand(PreparationUnlock.class, preparationId);
        command.execute();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Locked preparation #{}...", preparationId);
        }
    }

    @Override
    public void copyStepsFrom(String id, String from) {
        LOG.debug("copy preparations steps from {} to {}", from, id);

        final HystrixCommand<Void> command = getCommand(PreparationCopyStepsFrom.class, id, from);
        command.execute();

        LOG.info("preparation's steps copied from {} to {}", from, id);
    }

    @Override
    public void moveStep(final String preparationId, String stepId, String parentStepId) {
        LOG.info("Moving step {} after step {}, within preparation {}", stepId, parentStepId, preparationId);

        final HystrixCommand<String> command = getCommand(PreparationReorderStep.class, preparationId, stepId, parentStepId);
        command.execute();

        LOG.debug("Step {} moved after step {}, within preparation {}", stepId, parentStepId, preparationId);

    }

    // ---------------------------------------------------------------------------------
    // ----------------------------------------PREVIEW----------------------------------
    // ---------------------------------------------------------------------------------

    @Override
    public StreamingResponseBody previewDiff(PreviewDiffParameters input) {
        // get preparation details
        final Preparation preparation = internalGetPreparation(input.getPreparationId());
        final List<Action> lastActiveStepActions = internalGetActions(preparation.getId(), input.getCurrentStepId());
        final List<Action> previewStepActions = internalGetActions(preparation.getId(), input.getPreviewStepId());

        final HystrixCommand<InputStream> transformation = getCommand(PreviewDiff.class, input, preparation,
                lastActiveStepActions, previewStepActions);
        return executePreviewCommand(transformation);
    }

    @Override
    public StreamingResponseBody previewUpdate(PreviewUpdateParameters input) {
        // get preparation details
        final Preparation preparation = internalGetPreparation(input.getPreparationId());
        final List<Action> actions = internalGetActions(preparation.getId());

        final HystrixCommand<InputStream> transformation = getCommand(PreviewUpdate.class, input, preparation, actions);
        return executePreviewCommand(transformation);
    }

    @Override
    public StreamingResponseBody previewAdd(PreviewAddParameters input) {
        Preparation preparation = null;
        List<Action> actions = new ArrayList<>(0);

        // get preparation details with dealing with preparations
        if (StringUtils.isNotBlank(input.getPreparationId())) {
            preparation = internalGetPreparation(input.getPreparationId());
            actions = internalGetActions(preparation.getId());
        }

        final HystrixCommand<InputStream> transformation = getCommand(PreviewAdd.class, input, preparation, actions);
        return executePreviewCommand(transformation);
    }

    private StreamingResponseBody executePreviewCommand(HystrixCommand<InputStream> transformation) {
        return CommandHelper.toStreaming(transformation);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getPreparationColumnSemanticCategories(String preparationId, String columnId, String stepId) {
        LOG.debug("listing semantic types for preparation {} / {}, column {}", preparationId, columnId, stepId);
        return CommandHelper.toStreaming(getCommand(GetPreparationColumnTypes.class, preparationId, columnId, stepId));
    }

    /**
     * Helper method used to retrieve preparation actions via a hystrix command.
     *
     * @param preparationId the preparation id to get the actions from.
     * @return the preparation actions.
     */
    private List<Action> internalGetActions(String preparationId) {
        return internalGetActions(preparationId, "head");
    }

    /**
     * Helper method used to retrieve preparation actions via a hystrix command.
     *
     * @param preparationId the preparation id to get the actions from.
     * @param stepId the preparation version.
     * @return the preparation actions.
     */
    private List<Action> internalGetActions(String preparationId, String stepId) {
        final PreparationGetActions getActionsCommand = getCommand(PreparationGetActions.class, preparationId, stepId);
        try {
            return mapper.readerFor(new TypeReference<List<Action>>() {

            }).readValue(getActionsCommand.execute());
        } catch (IOException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_GET_PREPARATION_DETAILS, e);
        }
    }

    /**
     * Helper method used to get a preparation for internal class use.
     *
     * @param preparationId the preparation id.
     * @return the preparation.
     */
    private Preparation internalGetPreparation(String preparationId) {
        try {
            GenericCommand<InputStream> command = getCommand(PreparationDetailsGet.class, preparationId);
            return mapper.readerFor(Preparation.class).readValue(command.execute());
        } catch (IOException e) {
            throw new TDPException(UNABLE_TO_READ_PREPARATION, e, withBuilder().put("id", preparationId).build());
        }
    }

    private boolean isHeadStepDependingOnDeletedDataSet(Step step) {
        final boolean valid;
        // If root
        if (step.getParent() == null) {
            valid = true;
        } else {
            List<Action> actions = step.getContent().getActions();
            boolean oneActionRefersToNonexistentDataset = actions.stream() //
                    .filter(action -> StringUtils.equals(action.getName(), Lookup.LOOKUP_ACTION_NAME)) //
                    .map(action -> action.getParameters().get(Lookup.Parameters.LOOKUP_DS_ID.getKey())) //
                    .anyMatch(dsId -> {
                        boolean hasNoDataset;
                        try {
                            hasNoDataset = dataSetService.getMetadata(dsId) == null;
                        } catch (TDPException e) {
                            // Dataset could not be retrieved => Main reason is not present
                            LOG.debug("The data set could not be retrieved: " + e);
                            hasNoDataset = true;
                        }
                        return hasNoDataset;
                    });
            valid = !oneActionRefersToNonexistentDataset && isHeadStepDependingOnDeletedDataSet(step.getParent());
        }
        return valid;
    }

}
