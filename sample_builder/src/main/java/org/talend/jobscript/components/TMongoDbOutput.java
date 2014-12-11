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

import org.talend.StringsUtils;
import org.talend.jobscript.Column;

public class TMongoDbOutput extends AbstractComponent {

    private String host;

    private long port;

    private String database;

    private String collection;

    public TMongoDbOutput(String host, long port, String database, String collection) {
        super("tMongoDBOutput", "tMongoDBOutput_1");
        this.host = host;
        this.port = port;
        this.database = database;
        this.collection = collection;
    }

    @Override
    public String generate(int position) {
        String toReturn = "addComponent {" + "\n";

        toReturn += getComponentDefinition(position);

        toReturn += "setSettings {" + "\n";
        toReturn += "USE_EXISTING_CONNECTION : \"false\"," + "\n";
        toReturn += "DB_VERSION : \"MONGODB_2_6_X\"," + "\n";
        toReturn += "USE_REPLICA_SET : \"false\"," + "\n";
        toReturn += "HOST : \"\\\"" + host + "\\\"\"," + "\n";
        toReturn += "PORT : \"" + port + "\"," + "\n";
        toReturn += "DATABASE : \"\\\"" + database + "\\\"\"," + "\n";
        toReturn += "COLLECTION : \"\\\"" + collection + "\\\"\"," + "\n";
        toReturn += "REQUIRED_AUTHENTICATION : \"false\"," + "\n";
        toReturn += "DROP_COLLECTION_CREATE : \"true\"," + "\n";
        toReturn += "DATA_ACTION : \"insert\"," + "\n";
        toReturn += "DIE_ON_ERROR : \"false\"," + "\n";
        toReturn += "IS_VIRTUAL_COMPONENT : \"false\"," + "\n";
        toReturn += "REMOVE_ROOT : \"false\"," + "\n";
        toReturn += "COMPACT_FORMAT : \"false\"," + "\n";
        toReturn += "DATA_NODE : \"\\\"\\\"\"," + "\n";
        toReturn += "QUERY_NODE : \"\\\"\\\"\"," + "\n";

        toReturn += "MAPPING {" + "\n";
        for (Column column : inputSchema) {
            toReturn += "   SCHEMA_COLUMN : \"" + column.name + "\"," + "\n";
            toReturn += "   PARENT_NODE_PATH : \"\",\n";
        }
        toReturn = StringsUtils.removeLastChars(toReturn, 2);
        toReturn += "\n}," + "\n";

        toReturn += "}" + "\n";

        toReturn = addSchema(toReturn);

        toReturn += "}" + "\n";

        return toReturn;
    }

}
