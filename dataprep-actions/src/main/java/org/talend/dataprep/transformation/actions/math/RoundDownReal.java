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
package org.talend.dataprep.transformation.actions.math;

import org.talend.dataprep.transformation.actions.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;
import org.talend.dataprep.transformation.actions.common.DataprepAction;

import java.math.RoundingMode;

/**
 * Round towards zero. Never increments the digit prior to a discarded fraction (i.e. truncates)
 * 
 * @see RoundingMode#DOWN
 */
@DataprepAction(AbstractActionMetadata.ACTION_BEAN_PREFIX + RoundDownReal.ACTION_NAME)
public class RoundDownReal extends AbstractRound {

    /**
     * The action name. round_down was taken by RemoveFractionalPart action, and i don't want to change its id
     */
    public static final String ACTION_NAME = "round_down_real"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    protected RoundingMode getRoundingMode() {
        return RoundingMode.DOWN;
    }
}
