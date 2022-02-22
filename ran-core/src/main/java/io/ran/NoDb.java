package io.ran;

public class NoDb implements DbType {
	@Override
	public String getName() {
		return "No db";
	}
}
