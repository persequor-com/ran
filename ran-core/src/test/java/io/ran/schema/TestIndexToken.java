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
		return "'"+token.camelHump()+"'";
	}

	@Override
	public String unescaped() {
		return token.camelHump();
	}
}
