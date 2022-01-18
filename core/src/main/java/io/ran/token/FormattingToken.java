package io.ran.token;

public abstract class FormattingToken {
	protected Token token;
	protected String specifiedName = null;

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
