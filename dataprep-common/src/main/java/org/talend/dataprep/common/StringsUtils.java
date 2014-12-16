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
package org.talend.dataprep.common;

public class StringsUtils {

    public static String removeLastChars(String src, int n) {
        return src.substring(0, src.length() - n);
    }

    public static StringBuilder removeLastChars(StringBuilder src, int n) {
        return src.delete(src.length() - n, src.length());
    }
}
