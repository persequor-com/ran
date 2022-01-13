package io.ran.token;

public abstract class TableToken extends FormattingToken {
	protected TableToken(Token token) {
		super(token);
	}

	protected TableToken(String concreteName) {
		super(concreteName);
	}
}
