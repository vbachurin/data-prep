package org.talend.recipeProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.api.java.Row;
import org.talend.recipeProcessor.spark.Concat;
import org.talend.recipeProcessor.spark.Sort;
import org.talend.recipeProcessor.spark.SparkSqlOperation;
import org.talend.recipeProcessor.spark.UpperCase;

public class Spark {

    public static void main(String[] args) throws IOException {
        SparkConf conf = new SparkConf().setAppName("test").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> people = sc.textFile("/home/stef/talend/test_files/customers_10k.json");

        people.cache();
        System.out.println(people.count());

        // Model model = new Model();
        // model.table = "people";
        // model.columns = Arrays.asList("name", "age", "dept");
        //
        String asked = "";
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        // JavaRDD<String> step1 = new UpperCase("name").apply(people);
        // System.out.println(step1.collect());
        //
        // JavaRDD<String> step2 = new Concat(Arrays.asList("name", "dept"))
        // .apply(step1);
        // System.out.println(step2.collect());

        while (asked != null && !asked.equals("q")) {
            if (!asked.equals("")) {
                System.out.println(" - " + asked);

                SparkSqlOperation operation = null;
                if (asked.equals("upper")) {
                    operation = new UpperCase("firstname");
                } else if (asked.equals("concat")) {
                    operation = new Concat(Arrays.asList("firstname", "lastname"));
                } else if (asked.equals("sort")) {
                    operation = new Sort("city");
                }
                if (operation != null) {
                    long start = System.currentTimeMillis();
                    people = operation.apply(people);
                    List<String> collect = people.collect();
                    System.out.println("In " + (System.currentTimeMillis() - start) + " ms");
                    System.out.println(collect.subList(0, 10));

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
