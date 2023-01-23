/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
