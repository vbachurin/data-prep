package org.talend.recipeProcessor.spark;


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
}
