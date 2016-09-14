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

package org.talend.dataprep.transformation.actions.delete;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.talend.dataprep.transformation.actions.category.ActionCategory.DATA_CLEANSING;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.transformation.actions.AbstractMetadataBaseTest;
import org.talend.dataprep.transformation.actions.common.ImplicitParameters;
import org.talend.dataprep.transformation.api.action.ActionTestWorkbench;

public class DeleteEmptyRowsTest extends AbstractMetadataBaseTest {

    @Autowired
    private DeleteEmptyRows action;

    @Test
    public void should_be_in_data_cleansing_category() {
        // when
        final String name = action.getCategory();

        // then
        assertThat(name, is(DATA_CLEANSING.getDisplayName()));
    }

    @Test
    public void should_return_name() {
        // when
        final String name = action.getName();

        // then
        assertThat(name, is("delete_empty_rows"));
    }

    @Test
    public void should_delete_empty_rows() {
        // given
        Long rowId = 0L;

        // row 1
        Map<String, String> rowContent = new HashMap<>();
        rowContent.put("0000", "David");
        rowContent.put("0001", "Bowie");
        final DataSetRow row1 = new DataSetRow(rowContent);
        row1.setTdpId(rowId++);

        // row 2
        rowContent = new HashMap<>();
        rowContent.put("0000", "");
        rowContent.put("0001", "");
        final DataSetRow row2 = new DataSetRow(rowContent);
        row2.setTdpId(rowId++);

        // row 3
        rowContent = new HashMap<>();
        rowContent.put("0000", "John");
        rowContent.put("0001", "Lennon");
        final DataSetRow row3 = new DataSetRow(rowContent);
        row3.setTdpId(rowId++);

        // row 4
        rowContent = new HashMap<>();
        rowContent.put("0000", "Prince");
        rowContent.put("0001", "");
        final DataSetRow row4 = new DataSetRow(rowContent);
        row3.setTdpId(rowId++);

        // when
        final Map<String, String> deleteEmptyRowsParameters = new HashMap<>();
        deleteEmptyRowsParameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "dataset");
        final Action deleteEmptyRows = factory.create(action, deleteEmptyRowsParameters);
        ActionTestWorkbench.test(Arrays.asList(row1, row2, row3, row4), actionRegistry, deleteEmptyRows);

        // then
        assertThat(row1.isDeleted(), is(false));
        assertThat(row2.isDeleted(), is(true));
        assertThat(row3.isDeleted(), is(false));
        assertThat(row4.isDeleted(), is(false));
    }

    @Test
    public void should_consider_empty_spaces() {
        // given
        Long rowId = 0L;

        // row 1
        Map<String, String> rowContent = new HashMap<>();
        rowContent.put("0000", "David");
        rowContent.put("0001", "Bowie");
        final DataSetRow row1 = new DataSetRow(rowContent);
        row1.setTdpId(rowId++);

        // row 2
        rowContent = new HashMap<>();
        rowContent.put("0000", "");
        rowContent.put("0001", " ");
        final DataSetRow row2 = new DataSetRow(rowContent);
        row2.setTdpId(rowId++);

        // row 3
        rowContent = new HashMap<>();
        rowContent.put("0000", "John");
        rowContent.put("0001", "Lennon");
        final DataSetRow row3 = new DataSetRow(rowContent);
        row3.setTdpId(rowId++);

        // row 4
        rowContent = new HashMap<>();
        rowContent.put("0000", "Prince");
        rowContent.put("0001", "");
        final DataSetRow row4 = new DataSetRow(rowContent);
        row3.setTdpId(rowId++);

        // when
        final Map<String, String> deleteEmptyRowsParameters = new HashMap<>();
        deleteEmptyRowsParameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "dataset");
        final Action deleteEmptyRows = factory.create(action, deleteEmptyRowsParameters);
        ActionTestWorkbench.test(Arrays.asList(row1, row2, row3, row4), actionRegistry, deleteEmptyRows);

        // then
        assertThat(row1.isDeleted(), is(false));
        assertThat(row2.isDeleted(), is(true));
        assertThat(row3.isDeleted(), is(false));
        assertThat(row4.isDeleted(), is(false));
    }

    @Test
    public void should_consider_empty_tabs() {
        // given
        Long rowId = 0L;

        // row 1
        Map<String, String> rowContent = new HashMap<>();
        rowContent.put("0000", "David");
        rowContent.put("0001", "Bowie");
        final DataSetRow row1 = new DataSetRow(rowContent);
        row1.setTdpId(rowId++);

        // row 2
        rowContent = new HashMap<>();
        rowContent.put("0000", "");
        rowContent.put("0001", "    ");
        final DataSetRow row2 = new DataSetRow(rowContent);
        row2.setTdpId(rowId++);

        // row 3
        rowContent = new HashMap<>();
        rowContent.put("0000", "John");
        rowContent.put("0001", "Lennon");
        final DataSetRow row3 = new DataSetRow(rowContent);
        row3.setTdpId(rowId++);

        // row 4
        rowContent = new HashMap<>();
        rowContent.put("0000", "Prince");
        rowContent.put("0001", "");
        final DataSetRow row4 = new DataSetRow(rowContent);
        row3.setTdpId(rowId++);

        // when
        final Map<String, String> deleteEmptyRowsParameters = new HashMap<>();
        deleteEmptyRowsParameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "dataset");
        final Action deleteEmptyRows = factory.create(action, deleteEmptyRowsParameters);
        ActionTestWorkbench.test(Arrays.asList(row1, row2, row3, row4), actionRegistry, deleteEmptyRows);

        // then
        assertThat(row1.isDeleted(), is(false));
        assertThat(row2.isDeleted(), is(true));
        assertThat(row3.isDeleted(), is(false));
        assertThat(row4.isDeleted(), is(false));
    }

}