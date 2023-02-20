package io.ran;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

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
	public void testHasWildCard() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("wildcard", List.class, String.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		assertEquals(List.class, addMethod.getReturnType().clazz);
	}

	@Test
	public void testHasNeitherGenericFromMethodOrClass() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("method3", String.class, String.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
	}

	@Test
	public void testHasGenericFromClass_parameterized() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("param1", List.class, List.class).orElseThrow(RuntimeException::new);
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		assertEquals(List.class, addMethod.getReturnType().clazz);
		assertEquals(String.class, addMethod.getReturnType().generics.get(0).clazz);
	}

	@Test
	public void testHasGenericFromMethod_parameterized() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("param2", List.class, List.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(List.class, retType.clazz);
		assertEquals(Object.class, retType.generics.get(0).clazz);
	}

	@Test
	public void testHasBothGenericFromMethodAndClass_parameterized() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("mixed2", List.class, List.class).orElseThrow(RuntimeException::new);
		assertTrue(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		assertEquals(List.class, addMethod.getReturnType().clazz);
		assertEquals(String.class, addMethod.getReturnType().generics.get(0).clazz);
	}

	@Test
	public void testHasBothGenericFromMethodAndClass_parameterizedWithClosure() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("mixed3", List.class, List.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		assertEquals(List.class, addMethod.getReturnType().clazz);
		assertEquals(String.class, addMethod.getReturnType().generics.get(0).clazz);
	}

	public static class GenericTester<T> {
		public T method1(T input) { return null; }
		public <T2> T2 method2(T2 input) { return null; }
		public <T2> T mixed(T2 input) { return null; }
		public List<?> wildcard(String input) { return null; }
		public List<T> param1(List<T> input) { return null; }
		public <T2> List<T2> param2(List<T2> input) { return null; }
		public <T2> List<T> mixed2(List<T2> input) { return null; }
		public <T2 extends T> List<T2> mixed3(List<T2> input) { return null; }
		public String method3(String input) { return null; }
	}

	public static class GenericTesterImpl extends GenericTester<String> {

	}
}
