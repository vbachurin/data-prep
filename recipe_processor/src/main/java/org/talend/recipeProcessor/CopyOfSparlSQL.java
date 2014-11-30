package org.talend.recipeProcessor;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.api.java.JavaSQLContext;
import org.apache.spark.sql.api.java.JavaSchemaRDD;
import org.apache.spark.sql.api.java.Row;

public class CopyOfSparlSQL {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("test").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaSQLContext sqlContext = new org.apache.spark.sql.api.java.JavaSQLContext(
				sc);
		JavaSchemaRDD people = sqlContext
				.jsonFile("/home/stef/talend/test_files/people.json");

		people.printSchema();

		people.registerTempTable("people");

		JavaSchemaRDD youngs = sqlContext
				.sql("SELECT UPPER(name),dept as dept FROM people WHERE age < 30");
		List<String> youngsNames = youngs.map(new Function<Row, String>() {
			public String call(Row row) {
				return "Name: " + row.getString(0) + " (" + row.getString(1)
						+ ")";
			}
		}).collect();
		youngs.printSchema();
		youngs.registerTempTable("youngs");

		JavaSchemaRDD both = sqlContext
				.sql("SELECT * FROM youngs WHERE dept = 'R&D'");
		List<String> bothNames = both.map(
				(row) -> "Name: " + row.getString(0) + " (" + row.getString(1)
						+ ")").collect();

		System.out.println(youngsNames);
		System.out.println(bothNames);
	}
}
