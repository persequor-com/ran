package io.ran;

public interface Wrappee<WRAPPER extends WRAPPEE, WRAPPEE> {
	WRAPPEE wrappee();
	void wrappee(WRAPPEE wrappee);
}
