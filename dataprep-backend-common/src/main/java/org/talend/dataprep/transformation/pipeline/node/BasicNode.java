package org.talend.dataprep.transformation.pipeline.node;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.transformation.pipeline.*;

public class BasicNode implements Node, RuntimeNode {

    protected Link link;

    @Override
    public void receive(DataSetRow row, RowMetadata metadata) {
        link.exec().emit(row, metadata);
    }

    @Override
    public Link getLink() {
        return link;
    }

    @Override
    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public void signal(Signal signal) {
        link.exec().signal(signal);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitNode(this);
    }

    @Override
    public RuntimeNode exec() {
        return this;
    }
}
