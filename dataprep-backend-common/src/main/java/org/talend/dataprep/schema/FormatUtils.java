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

package org.talend.dataprep.schema;

import java.nio.charset.Charset;

/**
 * Many constants used during format detection.
 */
public class FormatUtils {


    /**
     * Buffer sizes used to detect CSV and Html formats
     */
    public static final int META_TAG_BUFFER_SIZE = 8192;

    /**
     * ASCII charset used to detect CSV and Html formats
     */
    public static final Charset ASCII = Charset.forName("US-ASCII");

    /**
     * The default encoding, for instance it is used for xls format
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * The default encoding used for unknown media type
     */
    public static final String UNKNOWN_MEDIA_TYPE = "application/octet-stream";

    /**
     * No arg constructor
     */
    private FormatUtils(){}

}
