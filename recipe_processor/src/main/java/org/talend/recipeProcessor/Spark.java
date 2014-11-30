package org.talend.recipeProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.api.java.Row;
import org.json.JSONObject;
import org.talend.recipeProcessor.spark.Model;

public class Spark {

	public static void main(String[] args) throws IOException {
		SparkConf conf = new SparkConf().setAppName("test").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> people = sc
				.textFile("/home/stef/talend/test_files/people.json");
//
//		Model model = new Model();
//		model.table = "people";
//		model.columns = Arrays.asList("name", "age", "dept");
//
//		String asked = "";
//		InputStreamReader isr = new InputStreamReader(System.in);
//		BufferedReader br = new BufferedReader(isr);

		JavaRDD<String> lineLengths = people
				.map(new Function<String, String>() {
					public String call(String s) {
						JSONObject json=new JSONObject(s);
						json.put("name", json.getString("name").toUpperCase());
						return json.toString();
					}
				});
		
		JavaRDD<String> lineLengths2 = lineLengths
				.map(new Function<String, String>() {
					public String call(String s) {
						JSONObject json=new JSONObject(s);
						json.put("c3", json.getString("name")+" + "+(json.has("dept")? json.getString("dept"):"--"));
						return json.toString();
					}
				});
		
		System.out.println(lineLengths.collect());System.out.println(lineLengths2.collect());

		// while (asked != null && !asked.equals("q")) {
		// if (!asked.equals("")) {
		// System.out.println(" - " + asked);
		//
		// SparkSqlOperation operation = null;
		// if (asked.equals("upper")) {
		// operation = new UpperCase("name");
		// } else if (asked.equals("concat")) {
		// operation = new Concat(Arrays.asList("name", "dept"));
		// }
		// if (operation != null) {
		// String sql = operation.buildSql(model);
		// System.out.println("### " + sql);
		// JavaSchemaRDD prep = sqlContext.sql(sql);
		// List<String> prepNames = prep.map(
		// (row) -> "Row: " + rowToString(row)).collect();
		// prep.printSchema();prep.registerTempTable("people");
		// System.out.println(prepNames);
		// }
		// }
		//
		// System.out.print(" ? ");
		// asked = br.readLine();
		// }
	}

	private static String rowToString(Row row) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < row.length(); i++) {
			sb.append(row.get(i) + "/");
		}
		return sb.toString();
	}
}
