package org.talend.dataprep.dataset.objects.type;

public class Types {

    public static final Type ANY     = new Type("any");

    public static final Type STRING  = new Type("string", ANY);

    public static final Type NUMERIC = new Type("numeric", ANY);

    public static final Type INTEGER = new Type("integer", NUMERIC);

    public static final Type DOUBLE  = new Type("double", NUMERIC);

    public static final Type FLOAT   = new Type("float", NUMERIC);

}
