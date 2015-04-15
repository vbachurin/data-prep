package org.talend.dataprep.api.dataset;

/**
 * Represents governance data of the dataset.
 */
public class DataSetGovernance {

    /* validation step of the certification: 0=nothing, 1=asked, 2=certified */
    private int certificationStep = 0;

    public int getCertificationStep() {
        return this.certificationStep;
    }

    public void setCertificationStep(int certificationStep) {
        this.certificationStep = certificationStep;
    }

}
