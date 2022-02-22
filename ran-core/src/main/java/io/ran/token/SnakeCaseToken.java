/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran.token;

public class SnakeCaseToken extends TokenType {
	public SnakeCaseToken() {}

	public SnakeCaseToken(Token token) {
		super(token);
	}

	public SnakeCaseToken(String token) {
		super(token);
	}

	@Override
	protected void parseString(String tokenString) {
		if (!tokenString.toLowerCase().equals(tokenString)) {
			throw new InvalidTokenException("Snake case must be lower cased. Invalid input was: "+tokenString);
		}
		this.token = Token.of(tokenString.split("_"));
	}

	@Override
	public String toString() {
		return String.join("_",token.parts);
	}
}
