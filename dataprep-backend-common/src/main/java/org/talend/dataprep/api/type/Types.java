package org.talend.dataprep.api.type;

public class Types {

    public static final Type ANY = new Type("any"); //$NON-NLS-1$

    public static final Type STRING = new Type("string", ANY); //$NON-NLS-1$

    public static final Type NUMERIC = new Type("numeric", ANY); //$NON-NLS-1$

    public static final Type INTEGER = new Type("integer", NUMERIC); //$NON-NLS-1$

    public static final Type DOUBLE = new Type("double", NUMERIC); //$NON-NLS-1$

    public static final Type FLOAT = new Type("float", NUMERIC); //$NON-NLS-1$

}
