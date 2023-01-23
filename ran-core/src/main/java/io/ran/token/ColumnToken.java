package io.ran.token;

import io.ran.Property;

public abstract class ColumnToken extends FormattingToken {
	protected ColumnToken(Property property) {
		super(property);
	}

	protected ColumnToken(Token token) {
		super(token);
	}

	protected ColumnToken(String concreteName) {
		super(concreteName);
	}
}
