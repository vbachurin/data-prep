package org.talend.dataprep.transformation.pipeline;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;

/**
 * Links together {@link Node nodes}. Each implementation is responsible for handling correct
 * {@link #emit(DataSetRow, RowMetadata)} and {@link #signal(Signal)} methods.
 */
public interface Link {

    /**
     * Visit the implementation of the {@link Link}.
     * 
     * @param visitor A {@link Visitor} to visit the whole pipeline structure.
     */
    void accept(Visitor visitor);

    RuntimeLink exec();
}
