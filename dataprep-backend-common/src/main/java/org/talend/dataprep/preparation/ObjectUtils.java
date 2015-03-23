package org.talend.dataprep.preparation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.*;
import java.util.*;

public class ObjectUtils {

    private ObjectUtils() {
    }

    /**
     * Returns a list of all steps available from <code>step</code> parameter.
     * @param step A {@link Step step}.
     * @param repository A {@link Repository version} repository.
     * @return A list of {@link Step step} id. Empty list if <code>step</code> parameter is <code>null</code>.
     * @see Step#id()
     * @see Step#getParent()
     */
    public static List<String> listSteps(Step step, Repository repository) {
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
    private static void __listSteps(List<String> versions, Step step, Repository repository) {
        if (step == null) {
            return;
        }
        versions.add(step.id());
        __listSteps(versions, repository.get(step.getParent(), Step.class), repository);
    }

    /**
     * Append content of <code>blob2</code> to <code>blob</code> and returns a new blob containing the merge of the
     * two {@link Blob blobs}.
     * @param blob A blob with JSON content.
     * @param content New JSON content to add to <code>blob</code>.
     * @return A new {@link Blob blob} containing the merge of the 2 {@link Blob blobs}.
     */
    public static JSONBlob append(JSONBlob blob, InputStream content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree1 = mapper.reader().readTree(blob.getContent());
            JsonNode tree2 = mapper.reader().readTree(content);
            __append(tree1, tree2);
            String blobContent = mapper.writer().writeValueAsString(tree1);
            return new JSONBlob(blobContent);
        } catch (IOException e) {
            throw new RuntimeException("Unable to append content to #" + blob.id(), e);
        }
    }

    // Internal method for recursion
    private static void __append(JsonNode node1, JsonNode node2) {
        Iterator<Map.Entry<String, JsonNode>> fields = node2.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            JsonNode value = next.getValue();
            if (value.isArray()) {
                ArrayNode array1 = (ArrayNode) node1.get(next.getKey());
                ArrayNode array2 = (ArrayNode) value;
                for (JsonNode jsonNode : array2) {
                    array1.add(jsonNode);
                }
            }
        }
    }

    private static void prettyPrint(Repository repository, Step step, OutputStream out) {
        if (step == null) {
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.append("\t\tStep (").append(step.id()).append(")").append("\n");
            writer.flush();
            Blob blob = repository.get(step.getContent(), Blob.class);
            prettyPrint(blob, out);
            prettyPrint(repository, repository.get(step.getParent(), Step.class), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void prettyPrint(Blob blob, OutputStream out) {
        if (blob == null) {
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.append("\t\t\tContent: ").append("\n");
            writer.append("======").append("\n");
            writer.append(blob.getContent()).append("\n");
            writer.append("======").append("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void prettyPrint(Repository repository, Preparation preparation, OutputStream out) {
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
