package io.ran.token;

import io.ran.Property;

public abstract class FormattingToken {
	protected Property property;
	protected Token token;
	protected String specifiedName = null;

	protected FormattingToken(Property property) {
		this.property = property;
		this.token = property.getToken();
	}

	protected FormattingToken(Token token) {
		this.token = token;
	}

	protected FormattingToken(String concreteName) {
		this.token = Token.get(concreteName);
		this.specifiedName = concreteName;
	}

	public Token getToken() {
		return token;
	}

	public Property getProperty() {
		return property;
	}

	@Override
	public String toString() {
		return specifiedName != null ? specifiedName : toSql();
	}

	public String name() {
		return specifiedName != null ? specifiedName : unescaped();
	}

	public abstract String toSql();

	public abstract String unescaped();
}
