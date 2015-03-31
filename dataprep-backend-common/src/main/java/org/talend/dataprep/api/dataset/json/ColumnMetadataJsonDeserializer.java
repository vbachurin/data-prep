package org.talend.dataprep.api.dataset.json;

import java.io.IOException;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;

class ColumnMetadataJsonDeserializer extends JsonDeserializer<ColumnMetadata> {

    public ColumnMetadataJsonDeserializer() {
    }

    @Override
    public ColumnMetadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TreeNode treeNode = jsonParser.readValueAsTree();
        ColumnMetadata.Builder builder = new ColumnMetadata.Builder();
        builder.name(((TextNode) treeNode.get("id")).asText()); //$NON-NLS-1$
        builder.type(Type.valueOf(((TextNode) treeNode.get("type")).asText().toUpperCase())); //$NON-NLS-1$
        TreeNode quality = treeNode.path("quality"); //$NON-NLS-1$
        if (quality != null) {
            builder.empty(((NumericNode) quality.get("empty")).asInt()); //$NON-NLS-1$
            builder.valid(((NumericNode) quality.get("valid")).asInt()); //$NON-NLS-1$
            builder.invalid(((NumericNode) quality.get("invalid")).asInt()); //$NON-NLS-1$
        }
        return builder.build();
    }

}
