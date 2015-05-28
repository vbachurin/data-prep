package org.talend.dataprep.api.type;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public enum Type {
    ANY("any"), //$NON-NLS-1$
    STRING("string", ANY), //$NON-NLS-1$
    CHAR("char", STRING), //$NON-NLS-1$
    NUMERIC("numeric", ANY), //$NON-NLS-1$
    INTEGER("integer", NUMERIC), //$NON-NLS-1$
    DOUBLE("double", NUMERIC), //$NON-NLS-1$
    FLOAT("float", NUMERIC), //$NON-NLS-1$
    BOOLEAN("boolean", ANY), //$NON-NLS-1$
    DATE("date", ANY); //$NON-NLS-1$

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
     * @return The type's super type, returned value is never null (top level type is returns itself). All other types
     * are expected at least to return {@link Type#ANY}.
     * @see Type#ANY
     */
    public Type getSuperType() {
        return superType;
    }

    /**
     * Returns the type hierarchy starting from this type. Calling this method on {@link Type#ANY} returns all
     * supported types.
     * 
     * @return The list of types assignable from this type, including this type (i.e. . Returned list is never empty
     * since it at least <code>this</code>.
     */
    public List<Type> list() {
        List<Type> list = new LinkedList<>();
        list.add( this );
        subTypes.forEach( type -> list.addAll(type.list() ));
        return list;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns whether <code>type</code> is contained in the list of subtypes from {@link Type#list()}.
     * 
     * @return true is <code>type</code> is contained in all subtypes including this type or false otherwise.
     */
    public boolean isAssignableFrom(Type type) {
        return list().contains(type);
    }

    /**
     * Returns the type accessible from {@link Type#ANY} with <code>name</code>.
     * 
     * @param name A non-null type name.
     * @return The {@link Type} type corresponding to <code>name</code>.
     * @throws IllegalArgumentException If <code>name</code> is <code>null</code> or if type does not exist.
     */
    public static Type get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        List<Type> types = ANY.list();

        Optional<Type> type = types.stream().filter( type1 -> type1.getName().equalsIgnoreCase(name) ).findFirst();

        if (type.isPresent()){
            return type.get();
        }

        // default type to String
        return STRING;
        //throw new IllegalArgumentException("Type '" + name + "' does not exist.");
    }

    /**
     * Allows to test existence of a type by name.
     * @param name A non-null type name.
     * @return <code>true</code> if type exists, <code>false</code> otherwise.
     * @see #get(String)
     */
    public static boolean has(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        List<Type> types = ANY.list();
        Optional<Type> type = types.stream().filter( type1 -> type1.getName().equalsIgnoreCase(name) ).findFirst();

        return type.isPresent();
    }
}
