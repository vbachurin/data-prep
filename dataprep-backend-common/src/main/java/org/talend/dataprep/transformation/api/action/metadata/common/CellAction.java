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

import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory;
import org.talend.dataprep.transformation.api.action.parameters.Item;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import javax.annotation.Nonnull;

import static org.talend.dataprep.transformation.api.action.metadata.category.ScopeCategory.COLUMNS;

/**
 * Base class for all cell action.
 */
public abstract class CellAction implements ActionMetadata {

    /** Name of column id parameters */
    public static final String COLUMN_ID = "column_id"; //$NON-NLS-1$
    /** Name of row id parameters */
    public static final String ROW_ID = "row_id"; //$NON-NLS-1$

    /** The parameter object for the column id. */
    public static final Parameter COLUMN_ID_PARAMETER = new Parameter(COLUMN_ID, Type.STRING.getName(), null);
    /** The parameter object for the row id. */
    public static final Parameter ROW_ID_PARAMETER = new Parameter(ROW_ID, Type.INTEGER.getName(), null);

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { COLUMN_ID_PARAMETER, ROW_ID_PARAMETER };
    }

    /**
     * @see ActionMetadata#getItems()
     */
    @Override
    @Nonnull
    public Item[] getItems() {
        return new Item[0];
    }

    /**
     * @see ActionMetadata#getScope()
     */
    public ScopeCategory getScope() {
        return COLUMNS;
    }
}
