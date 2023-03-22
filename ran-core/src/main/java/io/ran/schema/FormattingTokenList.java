/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.schema;

import io.ran.Property;
import io.ran.token.ColumnToken;
import io.ran.token.FormattingToken;
import io.ran.token.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FormattingTokenList<T extends FormattingToken> extends ArrayList<T> {
	public FormattingTokenList(List<T> list) {
		super(list);
	}

	public FormattingTokenList() {

	}

	public static <X extends FormattingToken> FormattingTokenList<X> of(Function<Token, X> formattingTokenGetter, List<Token> tokens) {
		return new FormattingTokenList<>(tokens.stream().map(formattingTokenGetter).collect(Collectors.toList()));
	}

	public static <X extends FormattingToken> FormattingTokenList<X> ofProperties(Function<Property, X> formattingTokenGetter, List<Property> properties) {
		return new FormattingTokenList<>(properties.stream().map(formattingTokenGetter).collect(Collectors.toList()));
	}

	public static <X extends FormattingToken> FormattingTokenList<X> ofProperties(Function<Property, X> formattingTokenGetter, Property... properties) {
		return ofProperties(formattingTokenGetter, Arrays.asList(properties));
	}

	public static FormattingTokenList<ColumnToken> empty() {
		return new FormattingTokenList<>();
	}

	public String join(String separator) {
		return stream().map(FormattingToken::toSql).collect(Collectors.joining(", "));
	}
}
