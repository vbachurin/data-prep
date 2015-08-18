package org.talend.dataprep.api.dataset;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataSetGovernance that = (DataSetGovernance) o;
        return Objects.equals(certificationStep, that.certificationStep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificationStep);
    }
}
