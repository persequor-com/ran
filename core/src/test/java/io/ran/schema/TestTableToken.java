package io.ran.schema;

import io.ran.token.TableToken;
import io.ran.token.Token;

public class TestTableToken extends TableToken {
	public TestTableToken(Token token) {
		super(token);
	}

	@Override
	public String toSql() {
		return "'"+token.CamelBack()+"'";
	}

	@Override
	public String unescaped() {
		return token.CamelBack();
	}
}
