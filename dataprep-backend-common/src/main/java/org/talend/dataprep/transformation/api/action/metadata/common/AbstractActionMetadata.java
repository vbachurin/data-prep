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
import static org.talend.dataprep.exception.error.TransformationErrorCodes.MISSING_TRANSFORMATION_SCOPE_PARAMETER;
import static org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for all single column action.
 */
public abstract class AbstractActionMetadata implements ActionMetadata {

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
    protected Long getRowId(final Map<String, String> parameters) {
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
     * @throws IllegalArgumentException if the scope parameter is missing
     */
    protected ScopeCategory getScope(final Map<String, String> parameters) {
        final ScopeCategory scope = ScopeCategory.from(parameters.get(SCOPE.getKey()));
        if (scope == null) {
            throw new IllegalArgumentException("Parameter '" + SCOPE.getKey() + "' is required for all actions");
        }
        return scope;
    }

    //------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------CHECKERS------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @see ActionMetadata#acceptScope(ScopeCategory)
     */
    @Override
    public boolean acceptScope(final ScopeCategory scope) {
        switch (scope) {
            case CELL:
                return this instanceof ICellAction;
            case LINE:
                return this instanceof ILineAction;
            case COLUMN:
                return this instanceof IColumnAction;
            case TABLE:
                return this instanceof ITableAction;
        }
        return false;
    }

    /**
     * Scope consistency checks
     * 1. scope has mandatory parameters
     * 2. scope is available for the current transformation
     *
     * @param scope      the transformation scope
     * @param parameters the transformation parameters
     */
    private void checkScopeConsistency(final ScopeCategory scope, final Map<String, String> parameters) {
        if (!scope.checkMandatoryParameters(parameters)) {
            throw new TDPException(MISSING_TRANSFORMATION_SCOPE_PARAMETER);
        }

        if (!this.acceptScope(scope)) {
            throw new IllegalArgumentException("The action " + this.getName() + " does not support the provided scope " + scope);
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
    protected abstract void beforeApply(final Map<String, String> parameters);

    /**
     * @see ActionMetadata#create(Map)
     */
    @Override
    public final Action create(final Map<String, String> parameters) {
        final ScopeCategory scope = getScope(parameters);
        checkScopeConsistency(scope, parameters);

        beforeApply(parameters);
        final Long rowId = getRowId(parameters);
        final String columnId = getColumnId(parameters);

        return builder().withRow((row, context) -> {
            switch (scope) {
                case CELL:
                    if (rowId != null && rowId.equals(row.getTdpId())) {
                        ((ICellAction) this).applyOnCell(row, context, parameters, rowId, columnId);
                    }
                    break;
                case COLUMN:
                    ((IColumnAction) this).applyOnColumn(row, context, parameters, columnId);
                    break;
                case LINE:
                    if (rowId != null && rowId.equals(row.getTdpId())) {
                        ((ILineAction) this).applyOnLine(row, context, parameters, rowId);
                    }
                    break;
                case TABLE:
                    ((ITableAction) this).applyOnTable(row, context, parameters);
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
