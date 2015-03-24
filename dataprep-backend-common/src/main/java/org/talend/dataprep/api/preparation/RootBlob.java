package org.talend.dataprep.api.preparation;

public class RootBlob extends JSONBlob {

    public static final JSONBlob INSTANCE = new RootBlob();

    private RootBlob() {
        super("{\"actions\":[]}");
    }
}
