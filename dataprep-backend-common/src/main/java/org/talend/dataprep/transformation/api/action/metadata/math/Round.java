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
package org.talend.dataprep.transformation.api.action.metadata.math;

import org.springframework.stereotype.Component;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;

/**
 * This will compute the absolute value for numerical columns.
 */
@Component(Round.ACTION_BEAN_PREFIX + Round.ROUND_ACTION_NAME)
public class Round extends AbstractRound {

    /** The action name. */
    public static final String ROUND_ACTION_NAME = "round"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return ROUND_ACTION_NAME;
    }

    protected int compute(double from) {
        return (int) Math.round(from);
    }
}
