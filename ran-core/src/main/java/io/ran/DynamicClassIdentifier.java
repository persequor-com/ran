/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import javax.lang.model.SourceVersion;

public class DynamicClassIdentifier {
	private final String identifier;

	private DynamicClassIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DynamicClassIdentifier other = (DynamicClassIdentifier) o;
		return identifier.equals(other.identifier);
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	public static DynamicClassIdentifier create(String identifier) {
		if(identifier == null || identifier.trim().length() == 0) {
			throw new IllegalArgumentException("Dynamic class identifier cannot be null or blank string");
		}
		// it is probably not a responsibility of Ran to make this validation, should be removed in the future
		if (!SourceVersion.isIdentifier("A" + identifier) || SourceVersion.isKeyword(identifier)) {
			throw new IllegalArgumentException(String.format("Invalid dynamic class identifier: `%s`. It should be a valid Java qualified name but can start with a number.", identifier));
		}
		return new DynamicClassIdentifier(identifier);
	}
}
