// ============================================================================
//
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

package org.talend.dataprep.preparation.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.talend.dataprep.api.preparation.Preparation;

public abstract class PreparationRepositoryTest {

    protected abstract Preparation getPreparation(String preparationName);

    protected abstract PreparationRepository getRepository();

    @Test
    public void shouldListOnlyWantedClass() {

        List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        // store preparations
        final List<Preparation> preparations = ids.stream() //
                .map(i -> getPreparation(String.valueOf(i))) //
                .collect(Collectors.toList());

        preparations.stream().forEach(prep -> getRepository().add(prep));

        // list all preparations
        final Collection<Preparation> actual = getRepository().listAll(Preparation.class);

        assertEquals(ids.size(), actual.size());
        preparations.stream().forEach(actual::contains);
    }

    @Test
    public void shouldListOnlyPreparationsForDatasets() {
        List<Integer> ids = Arrays.asList(1, 2, 3);

        // store preparations
        final List<Preparation> preparations = ids.stream() //
                .map(i -> getPreparation(String.valueOf(i))) //
                .collect(Collectors.toList());

        preparations.stream().forEach(prep -> getRepository().add(prep));

        // get preparation by name
        final Preparation expected = preparations.get(1);
        final Collection<Preparation> actual = getRepository().getByDataSet(expected.getDataSetId());

        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));

    }

}
