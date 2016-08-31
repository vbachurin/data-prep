//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.transformation.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.dataprep.api.export.ExportParameters.SourceType.FILTER;

import org.junit.Test;
import org.talend.dataprep.transformation.TransformationBaseTest;

/**
 * Unit test for the TransformationCacheKey.
 *
 * @see TransformationCacheKey
 */
public class TransformationCacheKeyTest extends TransformationBaseTest {

    @Test
    public void shouldGenerateKey() throws Exception {
        // when
        final TransformationCacheKey key = createTestDefaultKey();

        // then
        assertNotNull(key.getKey());
    }

    @Test
    public void shouldGenerateSameKey() throws Exception {
        // given
        final TransformationCacheKey key1 = createTestDefaultKey();
        final TransformationCacheKey key2 = createTestDefaultKey(); // same params

        // then
        assertEquals(key1.getKey(), key2.getKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void headNotAllowed() throws Exception {
        // when
        new TransformationCacheKey(
                "prep1",
                "123456789",
                "JSON",
                "head", // this is not allowed
                "params",
                FILTER,
                "user 1"
        );
    }

    private TransformationCacheKey createTestDefaultKey() {
        return new TransformationCacheKey(
                "prep1",
                "123456789",
                "JSON",
                "v1",
                "params",
                FILTER,
                "user 1"
        );
    }
}