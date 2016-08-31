package org.talend.dataprep.transformation.pipeline.node;

import org.talend.dataprep.transformation.pipeline.Visitor;

public class SourceNode extends BasicNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSource(this);
    }

}
