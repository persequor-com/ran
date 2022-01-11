package io.ran.token;

public interface FormattingToken {
	String toSql();
	String unescaped();
	Token getToken();
}
