package org.talend.dataprep.api.type;

import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class ExportTypeTest {

    private final ExportType[] exportTypes = ExportType.values();

    @Test
    public void testExtensions() throws Exception {
        Set<String> expectedExtensions = new HashSet<String>() {{
            add(".csv");
            add(".xls");
            add(".tde");
            add(".json");
        }};
        final Set<ExportType> unexpectedExtensions = Stream.of(exportTypes).filter(exportType -> !expectedExtensions.remove(exportType.getExtension())).collect(Collectors.toSet());
        MatcherAssert.assertThat("Exports types with untested extensions", unexpectedExtensions, empty());
    }

    @Test
    public void testMimeTypes() throws Exception {
        Set<String> expectedMimeTypes = new HashSet<String>() {{
            add("text/csv");
            add("application/vnd.ms-excel");
            add("application/tde");
            add("application/json");
        }};
        final Set<ExportType> unexpectedFormats = Stream.of(exportTypes).filter(exportType -> !expectedMimeTypes.remove(exportType.getMimeType())).collect(Collectors.toSet());
        MatcherAssert.assertThat("Exports types with untested MIME types", unexpectedFormats, empty());
    }


}
