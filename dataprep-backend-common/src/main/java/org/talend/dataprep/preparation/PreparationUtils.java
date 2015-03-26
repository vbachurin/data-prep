package org.talend.dataprep.preparation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PreparationUtils {

    private PreparationUtils() {
    }

    /**
     * Returns a list of all steps available from <code>step</code> parameter.
     * 
     * @param step A {@link Step step}.
     * @param repository A {@link PreparationRepository version} repository.
     * @return A list of {@link Step step} id. Empty list if <code>step</code> parameter is <code>null</code>.
     * @see Step#id()
     * @see Step#getParent()
     */
    public static List<String> listSteps(Step step, PreparationRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null.");
        }
        if (step == null) {
            return Collections.emptyList();
        }
        List<String> versions = new LinkedList<>();
        __listSteps(versions, step, repository);
        return versions;
    }

    // Internal method for recursion
    private static void __listSteps(List<String> versions, Step step, PreparationRepository repository) {
        if (step == null) {
            return;
        }
        versions.add(step.id());
        __listSteps(versions, repository.get(step.getParent(), Step.class), repository);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }
}
