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
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

import java.math.RoundingMode;

/**
 * This will compute the largest (closest to positive infinity) value that is less than or equal to the cell value and
 * is equal to a mathematical integer.
 *
 * @see Math#floor(double)
 */
@Component(Floor.ACTION_BEAN_PREFIX + Floor.FLOOR_ACTION_NAME)
public class Floor extends AbstractMath {

    /**
     * The action name.
     */
    public static final String FLOOR_ACTION_NAME = "floor"; //$NON-NLS-1$

    /**
     * @see ActionMetadata#getName()
     */
    @Override
    public String getName() {
        return FLOOR_ACTION_NAME;
    }

    @Override
    protected RoundingMode getRoundingMode() {
        return RoundingMode.FLOOR;
    }
}
