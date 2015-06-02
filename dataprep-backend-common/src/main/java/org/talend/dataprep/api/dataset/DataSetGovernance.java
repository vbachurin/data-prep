package org.talend.dataprep.api.dataset;

/**
 * Represents governance data of the dataset.
 */
public class DataSetGovernance {

    public enum Certification {
        NONE,
        PENDING,
        CERTIFIED
    }

    private Certification certificationStep = Certification.NONE;

    public Certification getCertificationStep() {
        return this.certificationStep;
    }

    public void setCertificationStep(Certification certificationStep) {
        this.certificationStep = certificationStep;
    }

}
