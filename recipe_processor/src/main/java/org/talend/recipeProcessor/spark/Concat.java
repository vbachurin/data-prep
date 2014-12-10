package org.talend.recipeProcessor.spark;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.json.JSONObject;

public class Concat implements SparkSqlOperation {

    List<String> columnNames;

    public Concat(List<String> columnNames) {
        super();
        this.columnNames = columnNames;
    }

    @Override
    public String buildSql(Model model) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String column : model.columns) {
            if (!first)
                sb.append(", ");
            first = false;
            sb.append(column);
        }

        sb.append(", (");
        first = true;
        for (String column : columnNames) {
            if (!first)
                sb.append(" + ");
            first = false;
            sb.append(column);
        }
        sb.append(")");

        return "SELECT " + sb.toString() + " FROM " + model.table;
    }

    @Override
    public JavaRDD<String> apply(JavaRDD<String> src) {
        return src.map(new Function<String, String>() {

            public String call(String s) {
                JSONObject json = new JSONObject(s);

                String value = "";
                for (String columnName : columnNames) {
                    value += (json.has(columnName) ? json.getString(columnName) : "--") + " ";
                }

                json.put("c3", value);
                return json.toString();
            }
        });
    }

}
