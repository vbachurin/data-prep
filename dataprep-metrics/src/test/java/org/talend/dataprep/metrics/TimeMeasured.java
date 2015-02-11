package org.talend.dataprep.metrics;

import org.springframework.stereotype.Component;

@Component
public class TimeMeasured {

    private int delay = 0;

    private boolean error;

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Timed
    public void run() {
        try {
            Thread.sleep(delay);
            if (error) {
                throw new Error("Expected failure!");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
