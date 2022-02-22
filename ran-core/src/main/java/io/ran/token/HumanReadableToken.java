package io.ran.token;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HumanReadableToken extends TokenType {
	public HumanReadableToken() {}

	public HumanReadableToken(String tokenString) {
		super(tokenString);
	}

	@Override
	protected void parseString(String token) {
		this.token = Token.of(Arrays.stream(token.toLowerCase().split("\\s")).collect(Collectors.toList()));
	}

	@Override
	public String toString() {
		return token.getParts().get(0).substring(0,1).toUpperCase()
				+token.getParts().get(0).substring(1).toLowerCase()
				+ token.getParts().stream().skip(1L).map((s) -> " "+s.toLowerCase()).collect(Collectors.joining());
	}
}
