package io.ran.schema;

import io.ran.Property;
import io.ran.token.ColumnToken;
import io.ran.token.FormattingToken;
import io.ran.token.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FormattingTokenList<T extends FormattingToken> extends ArrayList<T> {
	public FormattingTokenList(List<T> list) {
		super(list);
	}

	public FormattingTokenList() {

	}

	public static <X extends FormattingToken> FormattingTokenList<X> of(Function<Token, X> formattingTokenGetter, List<Token> tokens) {
		return new FormattingTokenList<>(tokens.stream().map(formattingTokenGetter).collect(Collectors.toList()));
	}

	public static <X extends FormattingToken> FormattingTokenList<X> of(Function<Token, X> formattingTokenGetter, Token... tokens) {
		return of(formattingTokenGetter, Arrays.asList(tokens));
	}

	public static <X extends FormattingToken> FormattingTokenList<X> ofProperties(Function<Property, X> formattingTokenGetter, List<Property> properties) {
		return new FormattingTokenList<>(properties.stream().map(formattingTokenGetter).collect(Collectors.toList()));
	}

	public static <X extends FormattingToken> FormattingTokenList<X> ofProperties(Function<Property, X> formattingTokenGetter, Property... properties) {
		return ofProperties(formattingTokenGetter, Arrays.asList(properties));
	}

	public static FormattingTokenList<ColumnToken> empty() {
		return new FormattingTokenList<>();
	}

	public String join(String separator) {
		return stream().map(FormattingToken::toSql).collect(Collectors.joining(", "));
	}
}
