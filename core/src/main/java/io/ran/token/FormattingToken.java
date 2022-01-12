package io.ran.token;

public interface FormattingToken {
	String toSql();
	default String prepared() {
		return unescaped();
	}
	String unescaped();
	Token getToken();
}
