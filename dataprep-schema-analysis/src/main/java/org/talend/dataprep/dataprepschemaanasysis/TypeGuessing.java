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
package org.talend.dataprep.dataprepschemaanasysis;

import java.io.File;

import com.google.common.io.Files;

/**
 * created by stef on Dec 22, 2014 Detailled comment
 *
 */
public class TypeGuessing {

    public static KnownTypes guessFileType(File file) {
        switch (Files.getFileExtension(file.getName()).toLowerCase()) {
        case "csv":
            return KnownTypes.CSV;
        case "xls":
            return KnownTypes.XLS;
        default:
            return null;
        }
    }
}
