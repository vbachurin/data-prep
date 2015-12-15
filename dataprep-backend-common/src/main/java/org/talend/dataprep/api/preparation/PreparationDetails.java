package org.talend.dataprep.api.preparation;

/**
 * Bean that extends Preparation used for json serialization towards frontend.
 * 
 * @see org.talend.dataprep.api.preparation.json.PreparationDetailsJsonSerializer
 */
public class PreparationDetails {

    /** The wrapped Preparation. */
    private Preparation preparation;

    /**
     * Default constructor.
     *
     * @param source the source preparation.
     */
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
