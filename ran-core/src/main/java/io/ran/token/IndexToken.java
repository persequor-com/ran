package io.ran.token;

public abstract class IndexToken extends FormattingToken {
	protected IndexToken(Token token) {
		super(token);
	}

	protected IndexToken(String concreteName) {
		super(concreteName);
	}
}
