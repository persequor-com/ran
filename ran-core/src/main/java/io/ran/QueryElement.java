/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
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

	public static QueryElement of(QueryType eq, Property.PropertyValueList values) {
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
