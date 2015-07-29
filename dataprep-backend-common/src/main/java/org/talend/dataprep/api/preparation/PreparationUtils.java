package org.talend.dataprep.api.preparation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

public class PreparationUtils {

    private PreparationUtils() {
    }

    /**
     * Returns a list of all steps available from <code>step</code> parameter. Since all preparations share the same
     * root, calling this method is equivalent to:
     * <code>
     * listSteps(step, PreparationActions.ROOT_CONTENT.getId(), repository);
     * </code>
     *
     * @param step A {@link Step step}.
     * @param repository A {@link PreparationRepository version} repository.
     * @return A list of {@link Step step} id. Empty list if <code>step</code> parameter is <code>null</code>.
     * @see Step#id()
     * @see Step#getParent()
     */
    public static List<String> listSteps(Step step, PreparationRepository repository) {
        return listSteps(step, PreparationActions.ROOT_CONTENT.getId(), repository);
    }

    /**
     * Returns a list of all steps available from <code>step</code> parameter.
     *
     * @param step A {@link Step step}.
     * @param limit An {@link Step step } id limit for the steps history. History will stop at {@link Step step} with
     *              this id.
     * @param repository A {@link PreparationRepository version} repository.
     * @return A list of {@link Step step} id. Empty list if <code>step</code> parameter is <code>null</code>.
     * @see Step#id()
     * @see Step#getParent()
     */
    public static List<String> listSteps(Step step, String limit, PreparationRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null.");
        }
        if (limit == null) {
            throw new IllegalArgumentException("Limit cannot be null.");
        }
        if (step == null) {
            return Collections.emptyList();
        }
        List<String> versions = new LinkedList<>();
        __listSteps(versions, limit, step, repository);
        return versions;
    }

    // Internal method for recursion
    private static void __listSteps(List<String> versions, String limit, Step step, PreparationRepository repository) {
        if (step == null) {
            return;
        }
        versions.add(0, step.id());
        if (limit.equals(step.getId())) {
            return;
        }
        __listSteps(versions, limit, repository.get(step.getParent(), Step.class), repository);
    }

    private static void prettyPrint(PreparationRepository repository, Step step, OutputStream out) {
        if (step == null) {
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.append("\t\tStep (").append(step.id()).append(")").append("\n");
            writer.flush();
            PreparationActions blob = repository.get(step.getContent(), PreparationActions.class);
            prettyPrint(blob, out);
            prettyPrint(repository, repository.get(step.getParent(), Step.class), out);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PRINT_PREPARATION, e);
        }
    }

    private static void prettyPrint(PreparationActions blob, OutputStream out) {
        if (blob == null) {
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.append("\t\t\tContent: ").append("\n");
            writer.append("======").append("\n");
            writer.append(blob.serializeActions()).append("\n");
            writer.append("======").append("\n");
            writer.flush();
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PRINT_PREPARATION, e);
        }
    }

    public static void prettyPrint(PreparationRepository repository, Preparation preparation, OutputStream out) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.append("Preparation (").append(preparation.id()).append(")\n");
            writer.append("\tData set: ").append(preparation.getDataSetId()).append("\n");
            writer.append("\tAuthor: ").append(preparation.getAuthor()).append("\n");
            writer.append("\tCreation date: ").append(String.valueOf(preparation.getCreationDate())).append("\n");
            writer.append("\tSteps:").append("\n");
            writer.flush();
            prettyPrint(repository, preparation.getStep(), out);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PRINT_PREPARATION, e);
        }
    }
}
