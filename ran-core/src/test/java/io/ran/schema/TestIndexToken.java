/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

import io.ran.Property;
import io.ran.token.IndexToken;
import io.ran.token.Token;

public class TestIndexToken extends IndexToken {
	public TestIndexToken(Token token) {
		super(token);
	}

	public TestIndexToken(Property property) {
		super(property);
	}

	@Override
	public String toSql() {
		return "'" + token.camelHump() + "'";
	}

	@Override
	public String unescaped() {
		return token.camelHump();
	}
}
