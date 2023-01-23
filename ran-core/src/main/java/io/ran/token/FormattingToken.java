/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.token;

import io.ran.Property;

public abstract class FormattingToken {
	protected Property property;
	protected Token token;
	protected String specifiedName = null;

	protected FormattingToken(Property property) {
		this.property = property;
		this.token = property.getToken();
	}

	protected FormattingToken(Token token) {
		this.token = token;
	}

	protected FormattingToken(String concreteName) {
		this.token = Token.get(concreteName);
		this.specifiedName = concreteName;
	}

	public Token getToken() {
		return token;
	}

	public Property getProperty() {
		return property;
	}

	@Override
	public String toString() {
		return specifiedName != null ? specifiedName : toSql();
	}

	public String name() {
		return specifiedName != null ? specifiedName : unescaped();
	}

	public abstract String toSql();

	public abstract String unescaped();
}
