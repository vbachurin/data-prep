// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.jobscript.components;

import java.util.List;

import org.talend.jobscript.Column;

public class TReservoirSampling extends AbstractComponent {

    private int size = 10;

    public TReservoirSampling(int size) {
        super("tReservoirSampling", "tReservoirSampling_1");
        this.size = size;
    }

    @Override
    public String generate() {
        String toReturn = "addComponent {" + "\n";

        toReturn += getComponentDefinition();

        toReturn += "setSettings {" + "\n";
        toReturn += "SAMPLE_SIZE: \"" + size + "\"," + "\n";
        toReturn += "RANDOM_SEED: \"12345678\"," + "\n";
        toReturn += "CONNECTION_FORMAT: \"row\"" + "\n";
        toReturn += "}" + "\n";

        toReturn = addSchema( toReturn);

        toReturn += "}" + "\n";

        return toReturn;
    }

	

}
