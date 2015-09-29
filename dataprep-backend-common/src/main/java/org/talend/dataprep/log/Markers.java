package org.talend.dataprep.log;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Markers {

    private Markers() {
    }

    public static Marker dataset(String id) {
        return MarkerFactory.getMarker("DS" + id);
    }
}
