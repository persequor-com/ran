/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import io.ran.token.Token;

public class CompoundKey {
	protected Property.PropertyValueList values = new Property.PropertyValueList<>();

	public static CompoundKey get() {
		return new CompoundKey();
	}

	public Property.PropertyValueList getValues() {
		return values;
	}

	public Object getValue(Token token) {
		return ((Property.PropertyValueList<?>) values).stream().filter(p -> p.getProperty().getToken().equals(token)).findFirst().map(pv -> pv.getValue()).orElse(null);
	}

	public void add(Property.PropertyValue<?> propertyValue) {
		this.values.add((Property.PropertyValue) propertyValue);
	}

	public void add(String snakeCase, Class type, Object value) {
		this.values.add(Property.get(snakeCase, Clazz.of(type)).value(value));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CompoundKey that = (CompoundKey) o;
		if (values.size() != that.values.size()) {
			return false;
		}
		return values.containsAll(that.values);
	}

	@Override
	public int hashCode() {
		return values != null ? values.hashCode() : 0;
	}
}
