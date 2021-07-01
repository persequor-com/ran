/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Token {
	List<String> parts = new ArrayList<>();

	private Token(List<String> parts) {
		if (parts.stream().anyMatch(String::isEmpty)) {
			throw new InvalidTokenException("Empty token part in: {"+String.join("}{",parts)+"}");
		}
		this.parts.addAll(parts);
	}

	static public Token get(String anyFormat) {
		if (anyFormat.substring(0,1).toUpperCase().equals(anyFormat.substring(0,1))) {
			return CamelCase(anyFormat);
		}
		if (anyFormat.toLowerCase().equals(anyFormat)) {
			return snake_case(anyFormat);
		}
		return camelHump(anyFormat);
	}

	static public Token of(String... parts) {
		return new Token(Arrays.asList(parts));
	}

	static public Token of(List<String> parts) {
		return new Token(parts);
	}

	static public TokenList list() {
		return new TokenList();
	}

	public static Token CamelCase(String tokenString) {
		return new CamelCaseToken(tokenString).toToken();
	}

	public static Token camelHump(String tokenString) {
		return new CamelHumpToken(tokenString).toToken();
	}

	public static Token snake_case(String tokenString) {
		return new SnakeCaseToken(tokenString).toToken();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Token token = (Token) o;

		return parts != null ? parts.equals(token.parts) : token.parts == null;
	}

	@Override
	public int hashCode() {
		return parts != null ? parts.hashCode() : 0;
	}

	public <T extends TokenType> String toString(Class<T> type) {
		try {
			T tokenType = type.newInstance();
			tokenType.setToken(this);
			return tokenType.toString();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InvalidTokenException(e);
		}
	}

	@Override
	public String toString() {
		return "{"+String.join("}{",parts)+"}";
	}

	public int stringLength() {
		return parts.stream().mapToInt(String::length).sum();
	}

	public List<String> getParts() {
		return parts;
	}

	public String snake_case() {
		return toString(SnakeCaseToken.class);
	}

	public String camelHump() {
		return toString(CamelHumpToken.class);
	}

	public String CamelBack() {
		return toString(CamelCaseToken.class);
	}

	public String javaGetter() {
		if (parts.get(0).length() == 1) {
			return camelHump();
		} else {
			return CamelBack();
		}
	}

	public boolean endsWith(String id) {
		return parts.get(parts.size()-1).equals(id);
	}

	public static class TokenList extends ArrayList<Token> {
		public void add(String snakeCase) {
			add(Token.snake_case(snakeCase));
		}
	}
}
