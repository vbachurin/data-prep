package org.talend.dataprep.api.preparation;

public class RootStep extends Step {

    public static final Step INSTANCE = new RootStep();

    private RootStep() {
        setContent(RootBlob.INSTANCE.id());
        setParent(null);
    }
}
