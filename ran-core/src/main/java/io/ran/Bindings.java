package io.ran;

import java.util.Map;

public interface Bindings {
	Map<Class, Class> bindings(ClassLoader classLoader) throws ClassNotFoundException;
}
