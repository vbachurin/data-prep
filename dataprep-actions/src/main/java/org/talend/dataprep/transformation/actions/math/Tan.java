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

import org.apache.commons.math3.util.FastMath;
import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataprep.transformation.actions.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.actions.common.DataprepAction;

import static org.talend.dataprep.transformation.actions.math.Tan.TAN_NAME;

/**
 * Create a new column with Tangent
 */
@DataprepAction(AbstractActionMetadata.ACTION_BEAN_PREFIX + TAN_NAME)
public class Tan extends AbstractMathNoParameterAction {

    protected static final String TAN_NAME = "tan_numbers";

    @Override
    protected String calculateResult(String columnValue) {
        double value = BigDecimalParser.toBigDecimal(columnValue).doubleValue();

        double result = FastMath.tan(value);

        return Double.isNaN(result) ? ERROR_RESULT : Double.toString(result);
    }

    @Override
    protected String getColumnNameSuffix() {
        return "tan";
    }

    @Override
    public String getName() {
        return TAN_NAME;
    }

}
