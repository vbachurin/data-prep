package org.talend.dataprep.exception;

public interface Messages {

    String getProduct();

    String getGroup();

    default String getCode() {
        return this.toString();
    }
}
