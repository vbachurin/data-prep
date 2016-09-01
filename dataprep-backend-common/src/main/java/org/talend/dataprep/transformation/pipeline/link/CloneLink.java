package org.talend.dataprep.transformation.pipeline.link;

import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.transformation.pipeline.*;

public class CloneLink implements Link, RuntimeLink {

    private final Node[] nodes;

    public CloneLink(Node... nodes) {
        this.nodes = nodes;
    }

    @Override
    public void emit(DataSetRow row, RowMetadata metadata) {
        for (Node node : nodes) {
            node.exec().receive(row.clone(), metadata.clone());
        }
    }

    @Override
    public void signal(Signal signal) {
        for (Node node : nodes) {
            node.exec().signal(signal);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCloneLink(this);
    }

    @Override
    public RuntimeLink exec() {
        return this;
    }

    public Node[] getNodes() {
        return nodes;
    }
}
