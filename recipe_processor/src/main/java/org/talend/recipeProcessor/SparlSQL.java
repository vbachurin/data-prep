package org.talend.recipeProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.api.java.JavaSQLContext;
import org.apache.spark.sql.api.java.JavaSchemaRDD;
import org.apache.spark.sql.api.java.Row;
import org.talend.recipeProcessor.spark.Concat;
import org.talend.recipeProcessor.spark.Model;
import org.talend.recipeProcessor.spark.SparkSqlOperation;
import org.talend.recipeProcessor.spark.UpperCase;

public class SparlSQL {

    public static void main(String[] args) throws IOException {
        SparkConf conf = new SparkConf().setAppName("test").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaSQLContext sqlContext = new org.apache.spark.sql.api.java.JavaSQLContext(sc);
        JavaSchemaRDD people = sqlContext.jsonFile("/home/stef/talend/test_files/people.json");

        people.printSchema();
        people.registerTempTable("people");

        Model model = new Model();
        model.table = "people";
        model.columns = Arrays.asList("name", "age", "dept");

        String asked = "";
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while (asked != null && !asked.equals("q")) {
            if (!asked.equals("")) {
                System.out.println(" - " + asked);

                SparkSqlOperation operation = null;
                if (asked.equals("upper")) {
                    operation = new UpperCase("name");
                } else if (asked.equals("concat")) {
                    operation = new Concat(Arrays.asList("name", "dept"));
                }
                if (operation != null) {
                    String sql = operation.buildSql(model);
                    System.out.println("### " + sql);
                    JavaSchemaRDD prep = sqlContext.sql(sql);
                    List<String> prepNames = prep.map((row) -> "Row: " + rowToString(row)).collect();
                    prep.printSchema();
                    prep.registerTempTable("people");
                    System.out.println(prepNames);
                }
            }

            System.out.print(" ? ");
            asked = br.readLine();
        }
    }

    private static String rowToString(Row row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length(); i++) {
            sb.append(row.get(i) + "/");
        }
        return sb.toString();
    }
}
