// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.preparation;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;
import static org.talend.daikon.version.VersionedRepositoryConfiguration.versionedItem;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Journal;
import org.talend.daikon.version.api.VersionedRepository;
import org.talend.dataprep.api.preparation.Preparation;

@Configuration
public class PreparationRepositoryConfiguration {

    @Bean
    public Journal journal(CrudRepository<DataEvent, String> eventStorage) {
        return new Journal() {

            @Override
            public void append(DataEvent dataEvent) {
                eventStorage.save(dataEvent);
            }

            @Override
            public Stream<DataEvent> log(String id) {
                final Iterator<DataEvent> iterator = eventStorage.findAll().iterator();
                return createStreamFromIterator(iterator) //
                        .filter(Objects::nonNull) //
                        .filter(e -> StringUtils.equals(id, e.getResourceId()));
            }
        };
    }

    @Bean
    public VersionedRepository<Preparation> versionedRepository(Journal journal, CrudRepository<Preparation, String> storage) {
        return versionedItem(Preparation.class) //
                .journal(journal) //
                .storage(storage) //
                .build();
    }
}
