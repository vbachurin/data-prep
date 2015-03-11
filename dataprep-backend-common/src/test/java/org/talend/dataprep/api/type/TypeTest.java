package org.talend.dataprep.api.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class TypeTest {

    @Test
    public void testList() {
        List<Type> anyList = Types.ANY.list();
        assertThat(anyList, hasItems(Types.ANY, Types.DOUBLE, Types.FLOAT, Types.INTEGER, Types.NUMERIC, Types.STRING, Types.BOOLEAN));
        List<Type> numericList = Types.NUMERIC.list();
        assertThat(numericList, hasItems(Types.DOUBLE, Types.FLOAT, Types.INTEGER, Types.NUMERIC));
    }

    @Test
    public void testTypeInheritance() throws Exception {
        Field[] fields = Types.class.getFields();
        for (Field field : fields) {
            Type type = (Type) field.get(null);
            // Assert the top level type is ANY for all types in Types class.
            Type lastType = type;
            Type topSuperType = type.getSuperType();
            while (topSuperType != null) {
                lastType = topSuperType;
                topSuperType = topSuperType.getSuperType();
            }
            assertThat(lastType, is(Types.ANY));
        }
    }

    @Test
    public void testName() {
        assertThat(Types.STRING.getName(), is("string"));
        assertThat(Types.STRING.getName(Locale.FRENCH), is("string"));
    }

    @Test
    public void testGetByNameArguments() {
        try {
            Types.get(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            Types.get("null");
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testGetByName() throws Exception {
        Type type = Types.get("string");
        assertNotNull(type);
        assertEquals("string", type.getName());
    }

    @Test
    public void testIsAssignableFrom() throws Exception {
        assertTrue(Types.NUMERIC.isAssignableFrom(Types.INTEGER));
        assertTrue(Types.NUMERIC.isAssignableFrom(Types.DOUBLE));
        assertFalse(Types.NUMERIC.isAssignableFrom(Types.STRING));
    }

}
