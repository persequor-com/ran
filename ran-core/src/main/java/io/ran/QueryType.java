package io.ran;

public class QueryType {
	private String type;

	public static QueryType eq = new QueryType("eq");
	public static QueryType lt = new QueryType("lt");
	public static QueryType gt = new QueryType("gt");
	public static QueryType isNull = new QueryType("isNull");
	public static QueryType withEager = new QueryType("withEager");
	public static QueryType subQuery = new QueryType("subQuery");
	public static QueryType subQueryList = new QueryType("subQueryList");

	public QueryType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
