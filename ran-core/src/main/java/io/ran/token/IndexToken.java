package io.ran.token;

import io.ran.Property;

public abstract class IndexToken extends FormattingToken {
	protected IndexToken(Property property) {
		super(property);
	}

	protected IndexToken(Token token) {
		super(token);
	}

	protected IndexToken(String concreteName) {
		super(concreteName);
	}
}
