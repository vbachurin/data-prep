package org.talend.recipeProcessor.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.json.JSONObject;

public class UpperCase implements SparkSqlOperation {

    String columnName;

    public UpperCase(String columnName) {
        super();
        this.columnName = columnName;
    }

    @Override
    public String buildSql(Model model) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String column : model.columns) {
            if (!first)
                sb.append(", ");
            first = false;
            if (column.equals(columnName))
                sb.append("UPPER(" + column + ") AS " + column);
            else
                sb.append(column);
        }

        return "SELECT " + sb.toString() + " FROM " + model.table;
    }

    @Override
    public JavaRDD<String> apply(JavaRDD<String> src) {
        return src.map(new Function<String, String>() {

            public String call(String s) {
                JSONObject json = new JSONObject(s);
                json.put(columnName, json.getString(columnName).toUpperCase());
                return json.toString();
            }
        });
    }
}
