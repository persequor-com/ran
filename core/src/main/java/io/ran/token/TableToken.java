package io.ran.token;

public abstract class TableToken implements FormattingToken {
	protected Token token;

	protected TableToken(Token token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return toSql();
	}
}
