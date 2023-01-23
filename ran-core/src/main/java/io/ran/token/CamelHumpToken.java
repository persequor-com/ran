/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran.token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CamelHumpToken extends TokenType {
	private static Pattern pattern = Pattern.compile("(?:[0-9]{2,}|[A-Z0-9])(?:[a-z0-9]*[a-z])*");

	public CamelHumpToken() {
	}

	public CamelHumpToken(Token token) {
		super(token);
	}

	public CamelHumpToken(String token) {
		super(token);
	}

	public static boolean is(String tokenString) {
		return tokenString.substring(0, 1).equals(tokenString.substring(0, 1).toLowerCase());
	}

	@Override
	protected void parseString(String tokenString) {
		if (!is(tokenString)) {
			throw new InvalidTokenException(tokenString + " does not match camelHumpNotation. It should start with lowercase");
		}
		Matcher matcher = pattern.matcher(tokenString);
		List<String> list = new ArrayList<>();
		if (matcher.find()) {
			list.add(tokenString.substring(0, matcher.start()).toLowerCase());
			list.add(matcher.group(0).toLowerCase());
		} else {
			list.add(tokenString);
		}
		while (matcher.find()) {
			list.add(matcher.group(0).toLowerCase());
		}
		token = Token.of(list);
		if (token.stringLength() != tokenString.length()) {
			throw new InvalidTokenException(tokenString + " does not match camelHumpNotation");
		}
	}

	@Override
	public String toString() {
		return token.parts.get(0) + token.parts.stream().skip(1).map(s -> s.substring(0, 1).toUpperCase() + (s.substring(1))).collect(Collectors.joining());
	}
}
