package io.ran.schema;

import io.ran.token.ColumnToken;
import io.ran.token.Token;

public class TestColumnToken extends ColumnToken {
	public TestColumnToken(Token token) {
		super(token);
	}

	@Override
	public String toSql() {
		return "'"+token.snake_case()+"'";
	}

	@Override
	public String unescaped() {
		return token.snake_case();
	}
}
