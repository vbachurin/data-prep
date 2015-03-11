// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.transformation.api.action.metadata;

import org.junit.Assert;
import org.junit.Test;

/**
 * created by stef on Mar 11, 2015
 * Detailled comment
 *
 */
public class NegateTest {

    @Test
    public void testToProperCase() {
        String expected = "Tagada";
        Assert.assertEquals(expected, Negate.toProperCase("TAGADA"));
        Assert.assertEquals(expected, Negate.toProperCase("tagada"));
        Assert.assertEquals(expected, Negate.toProperCase(expected));
        Assert.assertEquals(expected, Negate.toProperCase("tAGADA"));

        expected = "Voila Un (Beau) Canard";
        Assert.assertEquals(expected, Negate.toProperCase("Voila Un (beau) Canard"));
        Assert.assertEquals(expected, Negate.toProperCase("voila UN (BEAU) Canard"));
        Assert.assertEquals(expected, Negate.toProperCase("voila un (beau) canard"));
        Assert.assertEquals(expected, Negate.toProperCase("VOILA UN (Beau) CANARD"));
    }

}
