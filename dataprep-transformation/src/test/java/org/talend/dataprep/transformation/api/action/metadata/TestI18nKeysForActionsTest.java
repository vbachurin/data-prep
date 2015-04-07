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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.i18n.MessagesBundle;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.action.metadata.Item.Value;


/**
 * Test that a translation exists for i18n keys label/desc for each action and each params/item.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
public class TestI18nKeysForActionsTest {

    public static final Logger LOGGER = LoggerFactory.getLogger( TestI18nKeysForActionsTest.class );

    @Autowired
    private ActionMetadata[] allActions;

    private void assertI18nKeyExists(String key) {
        assertNotEquals("missing key <" + key + ">", key, MessagesBundle.getString(key));
    }

    @Test
    public void test() {
        for (ActionMetadata actionMetadata : allActions) {
            String name = actionMetadata.getName();
            assertNotNull(name);
            assertNotEquals("", name);

            String label = actionMetadata.getLabel();
            assertNotNull(label);
            assertNotEquals("", label);
            assertI18nKeyExists("action." + name + ".label");

            String desc = actionMetadata.getDescription();
            assertNotNull(desc);
            assertNotEquals("", desc);
            assertI18nKeyExists("action." + name + ".desc");

            String toString = actionMetadata.getName() + "," + actionMetadata.getCategory() + ","
                    + actionMetadata.getCompatibleColumnTypes() + "," + actionMetadata.getLabel() + ","
                    + actionMetadata.getDescription();
            LOGGER.info(toString);

            for (Parameter param : actionMetadata.getParameters()) {
                String pname = param.getName();
                assertNotNull(pname);
                assertNotEquals("", pname);

                String plabel = param.getLabel();
                assertNotNull(plabel);
                assertNotEquals("", plabel);
                assertI18nKeyExists("parameter." + pname + ".label");

                String pdesc = param.getDescription();
                assertNotNull(pdesc);
                assertNotEquals("", pdesc);
                assertI18nKeyExists("parameter." + pname + ".desc");

                LOGGER.trace("  - " + pname + " | " + plabel + " | " + pdesc);
            }

            for (Item item : actionMetadata.getItems()) {
                String pname = item.getName();
                assertNotNull(pname);
                assertNotEquals("", pname);

                String plabel = item.getLabel();
                assertNotNull(plabel);
                assertNotEquals("", plabel);
                assertI18nKeyExists("parameter." + pname + ".label");

                String pdesc = item.getDescription();
                assertNotNull(pdesc);
                assertNotEquals("", pdesc);
                assertI18nKeyExists("parameter." + pname + ".desc");

                LOGGER.trace("  - " + pname + " | " + plabel + " | " + pdesc);
                for (Value value : item.getValues()) {
                    LOGGER.trace("    - " + value);

                    for (Parameter param : value.getParameters()) {
                        String oname = param.getName();
                        assertNotNull(oname);
                        assertNotEquals("", oname);

                        String olabel = param.getLabel();
                        assertNotNull(olabel);
                        assertNotEquals("", olabel);
                        assertI18nKeyExists("parameter." + oname + ".label");

                        String odesc = param.getDescription();
                        assertNotNull(odesc);
                        assertNotEquals("", odesc);
                        assertI18nKeyExists("parameter." + oname + ".desc");

                        LOGGER.trace("      - " + oname + " | " + olabel + " | " + odesc);
                    }
                }
            }
            LOGGER.debug("");
        }
    }

}
