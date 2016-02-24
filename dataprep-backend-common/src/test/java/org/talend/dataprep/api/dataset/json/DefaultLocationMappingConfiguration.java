package org.talend.dataprep.api.dataset.json;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.api.dataset.DataSetLocation;
import org.talend.dataprep.api.dataset.location.HdfsLocation;
import org.talend.dataprep.api.dataset.location.HttpLocation;

/**
 * Declares default DataSetLocations for tests
 */
@Configuration
public class DefaultLocationMappingConfiguration {

    @Bean
    public DataSetLocationMapping httpLocationMapping() {
        return new DefaultDataSetLocationMapping(HttpLocation.NAME, HttpLocation.class);
    }

    @Bean
    public DataSetLocationMapping hdfsLocationMapping() {
        return new DefaultDataSetLocationMapping(HdfsLocation.NAME, HdfsLocation.class);
    }

    private static class DefaultDataSetLocationMapping implements DataSetLocationMapping{

        private final String locationType;

        private final Class<? extends DataSetLocation> locationClass;

        public DefaultDataSetLocationMapping(String locationType, Class<? extends DataSetLocation> locationClass) {
            this.locationType = locationType;
            this.locationClass = locationClass;
        }

        @Override
        public String getLocationType() {
            return locationType;
        }

        @Override
        public Class<? extends DataSetLocation> getLocationClass() {
            return locationClass;
        }
    }
}
