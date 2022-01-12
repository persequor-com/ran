package io.ran.token;

public interface FormattingToken {
	String toSql();
	default String name() {
		return unescaped();
	}
	String unescaped();
	Token getToken();
}
