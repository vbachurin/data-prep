package org.talend.recipeProcessor.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.json.JSONObject;

public class Sort implements SparkSqlOperation {

	String columnName;

	public Sort(String columnName) {
		super();
		this.columnName = columnName;
	}

	@Override
	public String buildSql(Model model) {
		return null;
	}

	@Override
	public JavaRDD<String> apply(JavaRDD<String> src) {
		return src.sortBy(new Function<String, String>() {

			@Override
			public String call(String s) throws Exception {
				JSONObject json = new JSONObject(s);
				json.put(columnName, json.getString(columnName).toUpperCase());
				if (json.has(columnName))
					return json.getString(columnName);
				else
					return "";
			}
		}, true, 1);
	}
}
