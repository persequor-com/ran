package io.ran.schema;

import io.ran.Property;
import io.ran.token.ColumnToken;
import io.ran.token.Token;

public class TestColumnToken extends ColumnToken {
	public TestColumnToken(Token token) {
		super(token);
	}

	public TestColumnToken(Property property) {
		super(property);
	}

	@Override
	public String toSql() {
		return "'" + token.snake_case() + "'";
	}

	@Override
	public String unescaped() {
		return token.snake_case();
	}
}
