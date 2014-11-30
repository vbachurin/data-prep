package org.talend.recipeProcessor.spark;

import java.util.List;

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

}
