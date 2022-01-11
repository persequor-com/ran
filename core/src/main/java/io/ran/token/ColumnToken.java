package io.ran.token;

public abstract class ColumnToken implements FormattingToken {
	protected Token token;

	protected ColumnToken(Token token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return toSql();
	}
}
