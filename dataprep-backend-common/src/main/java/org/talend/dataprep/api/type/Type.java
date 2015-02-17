package org.talend.dataprep.api.type;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Type {

    private final String name;

    private final Type superType;

    private final List<Type> subTypes = new LinkedList<>();

    Type(String name) {
        this(name, null);
    }

    Type(String name, Type superType) {
        this.name = name;
        this.superType = superType;
        if (superType != null) {
            superType.declareSubType(this);
        }
    }

    void declareSubType(Type type) {
        subTypes.add(type);
    }

    /**
     * @return A unique type name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The type name in given locale.
     */
    public String getName(Locale locale) {
        return name;
    }

    /**
     * @return The type's super type, returned value is never null (top level type is returns itself). All other types
     * are expected at least to return {@link Types#ANY}.
     * @see Types#ANY
     */
    public Type getSuperType() {
        return superType;
    }

    /**
     * Returns the type hierarchy starting from this type. Calling this method on {@link Types#ANY} returns all
     * supported types.
     * 
     * @return The list of types assignable from this type, including this type (i.e. . Returned list is never empty
     * since it at least <code>this</code>.
     */
    public List<Type> list() {
        List<Type> list = new LinkedList<>();
        list.add(this);
        for (Type subType : subTypes) {
            list.addAll(subType.list());
        }
        return list;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Type> getSubTypes() {
        return subTypes;
    }
}
