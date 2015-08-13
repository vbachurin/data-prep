// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.transformation.api.action.metadata.common;

import static org.talend.dataprep.api.preparation.Action.Builder.builder;
import static org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;
import org.talend.dataprep.transformation.api.action.validation.ActionMetadataValidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for all single column action.
 */
public abstract class AbstractActionMetadata implements ActionMetadata {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractActionMetadata.class);

    /** The validator. */
    @Autowired
    private ActionMetadataValidation validator;

    //------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------PARAMS GETTERS---------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the columnId from parameters
     *
     * @param parameters the transformation parameters
     * @return the column id
     */
    private String getColumnId(final Map<String, String> parameters) {
        return parameters.get(COLUMN_ID.getKey());
    }

    /**
     * Get the rowId from parameters
     *
     * @param parameters the transformation parameters
     * @return the row id
     */
    private Long getRowId(final Map<String, String> parameters) {
        final String rowIdAsString = parameters.get(ROW_ID.getKey());
        if (rowIdAsString != null) {
            return Long.parseLong(rowIdAsString);
        }
        return null;
    }

    /**
     * Get the scope category from parameters
     *
     * @param parameters the transformation parameters
     * @return the scope
     */
    private ScopeCategory getScope(final Map<String, String> parameters) {
        return ScopeCategory.from(parameters.get(SCOPE.getKey()));
    }

    //------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------CHECKERS------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @see ActionMetadata#acceptScope(ScopeCategory)
     */
    @Override
    public final boolean acceptScope(final ScopeCategory scope) {
        switch (scope) {
            case CELL:
            return this instanceof CellAction;
            case LINE:
            return this instanceof RowAction;
            case COLUMN:
            return this instanceof ColumnAction;
            case TABLE:
            return this instanceof DataSetAction;
        default:
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------EXECUTION------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Initialisation before transformation execution loop
     *
     * @param parameters The transformation parameters
     */
    protected void beforeApply(final Map<String, String> parameters) {
        // default empty implementation
    }

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public final Action create(final Map<String, String> parameters) {
        validator.checkScopeConsistency(this, parameters);
        beforeApply(parameters);

        final Long rowId = getRowId(parameters);
        final String columnId = getColumnId(parameters);
        final ScopeCategory scope = getScope(parameters);

        return builder().withRow((row, context) -> {
            switch (scope) {
                case CELL:
                    if (rowId != null && rowId.equals(row.getTdpId())) {
                    ((CellAction) this).applyOnCell(row, context, parameters, rowId, columnId);
                    }
                    break;
                case COLUMN:
                ((ColumnAction) this).applyOnColumn(row, context, parameters, columnId);
                    break;
                case LINE:
                    if (rowId != null && rowId.equals(row.getTdpId())) {
                    ((RowAction) this).applyOnRow(row, context, parameters, rowId);
                    }
                    break;
                case TABLE:
                ((DataSetAction) this).applyOnDataSet(row, context, parameters);
                break;
            default:
                LOGGER.warn("Is there a new action scope ??? {}", scope);
                    break;
            }
            return row;
        }).build();
    }

    //------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------PARAMS-------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public List<Parameter> getParameters() {
        return ImplicitParameters.getParameters();
    }

    /**
     * By default, no Item.
     *
     * @see ActionMetadata#getItems()
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    //------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------UTILS--------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Return the json statistics node.
     *
     * @param mapper jackson object mapper.
     * @param column the column metadata to work on.
     * @return the json statistics node.
     */
    protected JsonNode getStatisticsNode(ObjectMapper mapper, ColumnMetadata column) {
        try {
            return mapper.readTree(column.getStatistics());
        } catch (final IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

}
