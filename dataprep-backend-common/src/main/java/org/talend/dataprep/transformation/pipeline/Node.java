package org.talend.dataprep.transformation.pipeline;

import org.talend.dataprep.transformation.pipeline.link.NullLink;

/**
 * A node is a processing unit inside the transformation pipeline.
 */
public interface Node {

    /**
     * @return The {@link Link} to another Node. Never returns <code>null</code>, use {@link NullLink} instead.
     */
    Link getLink();

    /**
     * Changes the {@link Link link} for output processing of this node.
     *
     * @param link The {@link Link} to server as output for current node.
     */
    void setLink(Link link);

    /**
     * Visit the implementation of the {@link Node}.
     *
     * @param visitor A {@link Visitor} to visit the whole pipeline structure.
     */
    void accept(Visitor visitor);

    RuntimeNode exec();
}
