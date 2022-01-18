package io.ran.token;

public abstract class ColumnToken extends FormattingToken {
	protected ColumnToken(Token token) {
		super(token);
	}

	protected ColumnToken(String concreteName) {
		super(concreteName);
	}
}
