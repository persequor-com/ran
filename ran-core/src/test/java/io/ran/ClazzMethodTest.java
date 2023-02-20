package io.ran;

import junit.framework.TestCase;
import org.junit.Test;

public class ClazzMethodTest extends TestCase {

	@Test
	public void testHasGenericFromClass() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("method1", String.class, String.class).orElseThrow(RuntimeException::new);
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
	}

	@Test
	public void testHasGenericFromMethod() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("method2", Object.class, Object.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
	}

	@Test
	public void testHasBothGenericFromMethodAndClass() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("mixed", String.class, Object.class).orElseThrow(RuntimeException::new);
		assertTrue(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
	}

	@Test
	public void testHasNeitherGenericFromMethodOrClass() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("method3", String.class, String.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
	}

	public static class GenericTester<T> {
		public T method1(T input) { return null; }
		public <T2> T2 method2(T2 input) { return null; }
		public <T2> T mixed(T2 input) { return null; }
		public String method3(String input) { return null; }
	}

	public static class GenericTesterImpl extends GenericTester<String> {

	}
}
