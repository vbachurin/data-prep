package org.talend.recipeProcessor.spark;

import java.io.Serializable;

import org.apache.spark.api.java.JavaRDD;

public interface SparkSqlOperation extends Serializable {
	
	public String buildSql(Model model);
	
	public JavaRDD<String> apply(JavaRDD<String> src);
	
}
