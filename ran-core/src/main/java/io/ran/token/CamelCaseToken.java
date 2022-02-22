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

public class CamelCaseToken extends TokenType {
	private static Pattern pattern = Pattern.compile("(?:[0-9]{2,}|[A-Z0-9])(?:[a-z0-9]*[a-z])*");

	public CamelCaseToken() {}

	public CamelCaseToken(Token token) {
		super(token);
	}

	public CamelCaseToken(String token) {
		super(token);
	}

	@Override
	protected void parseString(String tokenString) {
		if (!tokenString.substring(0,1).equals(tokenString.substring(0,1).toUpperCase())) {
			throw new InvalidTokenException(tokenString+" does not match CamelBackNotation. It should start with uppercase");
		}
		Matcher matcher = pattern.matcher(tokenString);
		List<String> list = new ArrayList<>();
		if (matcher.find()) {
			list.add(matcher.group(0).toLowerCase());
		}
		while(matcher.find()) {
			list.add(matcher.group(0).toLowerCase());
		}
		token = Token.of(list);
		if (token.stringLength() != tokenString.length()) {
			throw new InvalidTokenException(tokenString+ " does not match CamelBackNotation. Input: "+tokenString+" output token: "+token.toString());
		}
	}

	@Override
	public String toString() {
		return token.parts.stream().map(s -> s.substring(0,1).toUpperCase()+(s.substring(1))).collect(Collectors.joining());
	}
}
