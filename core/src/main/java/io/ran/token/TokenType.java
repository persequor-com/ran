/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran.token;

public abstract class TokenType {
	protected Token token;

	protected TokenType() {}

	protected void setToken(Token token) {
		this.token = token;
	}

	public TokenType(Token token) {
		this.token = token;
	}

	public TokenType(String token) {
		parseString(token);
	}

	protected abstract void parseString(String token);
	public abstract String toString();
	public Token toToken() {
		return token;
	}
}
