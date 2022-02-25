package io.ran;

public class DefaultRanConfig implements RanConfig {
	@Override
	public boolean enableRanClassesDebugging() {
		return false;
	}

	@Override
	public String projectBasePath() {
		return null;
	}
}
