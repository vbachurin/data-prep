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

package org.talend.dataprep.transformation.actions.clear;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.actions.category.ActionScope;
import org.talend.dataprep.transformation.actions.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;
import org.talend.dataprep.transformation.actions.common.ColumnAction;
import org.talend.dataprep.transformation.actions.common.DataprepAction;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.talend.dataprep.transformation.actions.category.ActionCategory.DATA_CLEANSING;
import static org.talend.dataprep.transformation.actions.category.ActionScope.INVALID;

/**
 * Clear cell when value is invalid.
 */
@DataprepAction(AbstractActionMetadata.ACTION_BEAN_PREFIX + ClearInvalid.ACTION_NAME)
public class ClearInvalid extends AbstractClear implements ColumnAction {

    /** the action name. */
    public static final String ACTION_NAME = "clear_invalid"; //$NON-NLS-1$

    private static final List<ActionScope> ACTION_SCOPE = Collections.singletonList(INVALID);

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ACTION_NAME;
    }

    /**
     * @see ActionMetadata#getCategory()
     */
    @Override
    public String getCategory() {
        return DATA_CLEANSING.getDisplayName();
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        return true;
    }

    /**
     * @see ActionMetadata#getActionScope()
     */
    @Override
    public List<ActionScope> getActionScope() {
        return ACTION_SCOPE;
    }

    @Override
    public boolean toClear(ColumnMetadata colMetadata, String value, ActionContext context) {
        return colMetadata.getQuality().getInvalidValues().contains(value);
    }

    @Override
    public Set<Behavior> getBehavior() {
        final EnumSet<Behavior> behaviors = EnumSet.copyOf(super.getBehavior());
        behaviors.add(Behavior.NEED_STATISTICS);
        return behaviors;
    }
}
