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
package org.talend.dataprep.transformation.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class MessagesBundle {

    public static String getString(Locale locale, String i18nKey) {
        try {
            return ResourceBundle.getBundle("Messages", locale).getString(i18nKey);
        } catch (MissingResourceException e) {
            return i18nKey;
        }
    }

}
