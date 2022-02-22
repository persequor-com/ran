package io.ran;

public class QueryElement {
	Property.PropertyValueList values;
	QueryType queryType;
	Property.PropertyValue value;

	public QueryElement(QueryType eq, Property.PropertyValueList values) {
		queryType = eq;
		this.values = values;
	}

	public QueryElement(QueryType eq, Property.PropertyValue value) {
		queryType = eq;
		this.value = value;
	}

	public static QueryElement of(QueryType eq, Property.PropertyValue value) {
		return new QueryElement(eq, value);
	}

	public static QueryElement of(QueryType eq, Property.PropertyValueList  values) {
		return new QueryElement(eq, values);
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public Property.PropertyValue getValue() {
		return value;
	}

	public Property.PropertyValueList getValues() {
		return values;
	}
}
