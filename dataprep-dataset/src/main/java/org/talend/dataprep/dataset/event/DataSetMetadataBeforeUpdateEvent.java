package org.talend.dataprep.dataset.event;

import org.springframework.context.ApplicationEvent;
import org.talend.dataprep.api.dataset.DataSetMetadata;

/**
 * An event to indicate a data set metadata has been updated (and update has completed).
 */
public class DataSetMetadataBeforeUpdateEvent extends ApplicationEvent {

    public DataSetMetadataBeforeUpdateEvent(DataSetMetadata source) {
        super(source);
    }

    @Override
    public DataSetMetadata getSource() {
        return (DataSetMetadata) super.getSource();
    }
}
