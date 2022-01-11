package io.ran.token;

public abstract class IndexToken implements FormattingToken {
	protected Token token;

	protected IndexToken(Token token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return toSql();
	}
}
