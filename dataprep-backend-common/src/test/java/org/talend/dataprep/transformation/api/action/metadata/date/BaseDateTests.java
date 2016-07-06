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

package org.talend.dataprep.transformation.api.action.metadata.date;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.AbstractMetadataBaseTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all date related unit tests.
 */
public abstract class BaseDateTests extends AbstractMetadataBaseTest {

    /**
     * @param statisticsFileName the statistics file name to use.
     * @return a row with default settings for the tests.
     */
    protected DataSetRow getDefaultRow(String statisticsFileName) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        final Statistics statistics = mapper.readerFor(Statistics.class)
                .readValue(getDateTestJsonAsStream(statisticsFileName));

        Map<String, String> values = new HashMap<>();
        values.put("0000", "lorem bacon");
        values.put("0001", "01/01/2010");
        values.put("0002", "Bacon");

        final DataSetRow row = new DataSetRow(values);
        row.getRowMetadata().getById("0000").setType(Type.STRING.getName());
        row.getRowMetadata().getById("0000").setName("recipe");
        row.getRowMetadata().getById("0001").setType(Type.DATE.getName());
        row.getRowMetadata().getById("0001").setName("last update");
        row.getRowMetadata().getById("0001").setStatistics(statistics);
        row.getRowMetadata().getById("0002").setType(Type.STRING.getName());
        row.getRowMetadata().getById("0002").setName("steps");

        return row;
    }

    protected InputStream getDateTestJsonAsStream(String testFileName) {
        return BaseDateTests.class.getResourceAsStream(testFileName);
    }

}
