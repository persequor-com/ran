/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CamelCaseToken extends TokenType {
	private static Pattern pattern = Pattern.compile("(?:[0-9]{2,}|[A-Z0-9])(?:[a-z0-9]*[a-z])*");

	public CamelCaseToken() {
	}

	public CamelCaseToken(Token token) {
		super(token);
	}

	public CamelCaseToken(String token) {
		super(token);
	}

	@Override
	protected void parseString(String tokenString) {
		if (!tokenString.substring(0, 1).equals(tokenString.substring(0, 1).toUpperCase())) {
			throw new InvalidTokenException(tokenString + " does not match CamelBackNotation. It should start with uppercase");
		}
		Matcher matcher = pattern.matcher(tokenString);
		List<String> list = new ArrayList<>();
		if (matcher.find()) {
			list.add(matcher.group(0).toLowerCase());
		}
		while (matcher.find()) {
			list.add(matcher.group(0).toLowerCase());
		}
		token = Token.of(list);
		if (token.stringLength() != tokenString.length()) {
			throw new InvalidTokenException(tokenString + " does not match CamelBackNotation. Input: " + tokenString + " output token: " + token.toString());
		}
	}

	@Override
	public String toString() {
		return token.parts.stream().map(s -> s.substring(0, 1).toUpperCase() + (s.substring(1))).collect(Collectors.joining());
	}
}
