package org.talend.dataprep.api.preparation;

/**
 * Bean that extends Preparation used for json serialization towards frontend.
 */
public class PreparationDetails {

    private Preparation preparation;

    public PreparationDetails(Preparation source) {
        this.preparation = source;
    }

    /**
     * @return the Preparation
     */
    public Preparation getPreparation() {
        return preparation;
    }
}
