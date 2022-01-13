package io.ran.token;

public abstract class FormattingToken {
	protected Token token;
	private String concreteName = null;

	protected FormattingToken(Token token) {
		this.token = token;
	}

	protected FormattingToken(String concreteName) {
		this.token = Token.get(concreteName);
		this.concreteName = concreteName;
	}

	public Token getToken() {
		return token;
	}

	@Override
	public String toString() {
		return concreteName != null ? concreteName : toSql();
	}

	public String name() {
		return unescaped();
	}

	public abstract String toSql();
	public abstract String unescaped();
}
