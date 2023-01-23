/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

import io.ran.token.TableToken;
import io.ran.token.Token;

public class TestTableToken extends TableToken {
	public TestTableToken(Token token) {
		super(token);
	}

	@Override
	public String toSql() {
		return "'" + token.CamelBack() + "'";
	}

	@Override
	public String unescaped() {
		return token.CamelBack();
	}
}
