package io.ran;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

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
		Clazz<?> paramType = addMethod.parameters().get(0).getClazz();
		assertEquals(List.class, paramType.clazz);
		assertEquals(String.class, paramType.generics.get(0).clazz);
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
	public void testParamNonGeneric() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("paramNonGeneric", List.class, List.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(List.class, retType.clazz);
		assertEquals(String.class, retType.generics.get(0).clazz);
		Clazz<?> paramType = addMethod.parameters().get(0).getClazz();
		assertEquals(List.class, paramType.clazz);
		assertEquals(String.class, paramType.generics.get(0).clazz);
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

	@Test
	public void testHasWildCard_nested() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("nestedWildcard", NestedSelf.class, NestedSelf.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		assertEquals(NestedSelf.class, addMethod.getReturnType().clazz);
	}

	@Test
	public void testHasGenericFromClass_nested() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("nested1", NestedSelfSub.class, NestedSelfSub.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(3, retType.methods().size());
		assertEquals(NestedSelfSub.class, retType.clazz);
		//FIXME: assertEquals(NestedSelfSub.class, retType.generics.get(0).clazz);
	}

	@Test
	public void testHasGenericFromMethod_nested() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("nested2", NestedSelf.class, NestedSelf.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(3, retType.methods().size());
		assertEquals(NestedSelf.class, retType.clazz);
		//FIXME: assertEquals(NestedSelf.class, retType.generics.get(0).clazz);
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
		public List<String> paramNonGeneric(List<String>  input) { return null; }

		public NestedSelf<?> nestedWildcard(NestedSelf<?> input) { return null; }
		public NestedSelfSub nested1(NestedSelfSub input) { return null; }
		public <T2 extends NestedSelf<T2>> T2 nested2(T2 input) { return null; }
	}

	public static class GenericTesterImpl extends GenericTester<String> {

	}

	public interface NestedSelf0<K0 extends NestedSelf0<K0>> extends Consumer<K0> {
		K0 method(K0 input);
	}

	public interface NestedSelf<K extends NestedSelf<K>> extends NestedSelf0<K> {
		K method(K input);
	}

	public interface NestedSelfSub extends NestedSelf<NestedSelfSub> {

	}

}
