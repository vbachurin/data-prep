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

import com.google.refine.importers.SeparatorBasedImporter;
import com.google.refine.importers.SeparatorBasedImporter.Separator;

/**
 * created by stef on Dec 22, 2014 Detailled comment
 *
 */
public class GuessSeparator {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        File file = new File("/home/stef/talend/test_files/customers_1k.csv");
        Separator guessSeparator = SeparatorBasedImporter.guessSeparator(file, null);
        System.out.println(guessSeparator.separator + " in " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        file = new File("/home/stef/talend/test_files/customers_10k.csv");
        guessSeparator = SeparatorBasedImporter.guessSeparator(file, null);
        System.out.println(guessSeparator.separator + " in " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        file = new File("/home/stef/talend/test_files/customers_100k.csv");
        guessSeparator = SeparatorBasedImporter.guessSeparator(file, null);
        System.out.println(guessSeparator.separator + " in " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        file = new File("/home/stef/talend/test_files/customers_10k_comma.csv");
        guessSeparator = SeparatorBasedImporter.guessSeparator(file, null);
        System.out.println(guessSeparator.separator + " in " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        file = new File("/home/stef/talend/test_files/customers_10k_space.csv");
        guessSeparator = SeparatorBasedImporter.guessSeparator(file, null);
        System.out.println("<" + guessSeparator.separator + "> in " + (System.currentTimeMillis() - start) + " ms");

    }
}
