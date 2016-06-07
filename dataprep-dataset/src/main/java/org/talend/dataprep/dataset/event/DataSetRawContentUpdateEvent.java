package org.talend.dataprep.dataset.event;

import org.springframework.context.ApplicationEvent;
import org.talend.dataprep.api.dataset.DataSetMetadata;

public class DataSetRawContentUpdateEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DataSetRawContentUpdateEvent(DataSetMetadata source) {
        super(source);
    }

    @Override
    public DataSetMetadata getSource() {
        return (DataSetMetadata) super.getSource();
    }
}
