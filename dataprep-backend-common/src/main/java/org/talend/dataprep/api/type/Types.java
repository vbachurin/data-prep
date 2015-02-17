package org.talend.dataprep.api.type;

public class Types {

    public static final Type ANY = new Type("any"); //$NON-NLS-1$

    public static final Type STRING = new Type("string", ANY); //$NON-NLS-1$

    public static final Type NUMERIC = new Type("numeric", ANY); //$NON-NLS-1$

    public static final Type INTEGER = new Type("integer", NUMERIC); //$NON-NLS-1$

    public static final Type DOUBLE = new Type("double", NUMERIC); //$NON-NLS-1$

    public static final Type FLOAT = new Type("float", NUMERIC); //$NON-NLS-1$

    /**
     * Returns the type accessible from {@link Types#ANY} with <code>name</code>.
     * @param name A non-null type name.
     * @return The {@link Type} type corresponding to <code>name</code>.
     * @throws IllegalArgumentException If <code>name</code> is <code>null</code> or if type does not exist.
     */
    public static Type get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        Type type = _get(ANY, name);
        if (type == null) {
            throw new IllegalArgumentException("Type '" + name + "' does not exist.");
        }
        return type;
    }

    // Internal method for recursion
    private static Type _get(Type type, String name) {
        if (name.equals(type.getName())) {
            return type;
        }
        Type subTreeType = null;
        for (Type subType : type.getSubTypes()) {
            subTreeType = _get(subType, name);
            if (subTreeType != null) {
                break;
            }
        }
        return subTreeType;
    }
}
