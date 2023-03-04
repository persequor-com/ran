package io.ran;

import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

import static io.ran.ClazzTest.assertClazz;
import static org.junit.Assert.*;

public class ClazzMethodTest {

	@Test
	public void testGg() {
		Clazz<Mike> clazz = Clazz.of(Mike.class);
		assertTrue(clazz.methods().get(0).hasGenericFromClass()); // todo more tests?
	}

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
		// 		public <T2 extends T> List<T2> mixed3(List<T2> input) { return null; }
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("mixed3", List.class, List.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());

		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(List.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(String.class, retType.generics.get(0).clazz);

		Clazz<?> paramType = addMethod.parameters().get(0).getClazz();
		assertEquals(List.class, paramType.clazz);
		assertEquals(1, paramType.generics.size());
		assertEquals(String.class, paramType.generics.get(0).clazz);
	}

	@Test
	public void testHasWildCard_nested() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("nestedWildcard", NestedSelf.class, NestedSelf.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		assertEquals(NestedSelf.class, addMethod.getReturnType().clazz);
	}

	@Test
	public void testHasBoundWildCard_nested() throws InvocationTargetException, IllegalAccessException {
		// public NestedSelf<? extends NestedSelf<?>> nestedBoundWildcard(NestedSelf<? extends NestedSelf<?>> input) { return input; }
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("nestedBoundWildcard", NestedSelf.class, NestedSelf.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());


		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(NestedSelf.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(NestedSelf.class, retType.generics.get(0).clazz);

		Clazz<?> paramType = addMethod.parameters().get(0).getClazz();
		assertEquals(NestedSelf.class, paramType.clazz);
		assertEquals(1, paramType.generics.size());
		assertEquals(NestedSelf.class, paramType.generics.get(0).clazz);

		// Try calling the method, to verify that the types actually works at runtime
		NestedSelf<?> ns = new NestedSelfImpl<>();
		Object actual = addMethod.getMethod().invoke(new GenericTesterImpl(), ns);
		assertSame(ns, actual);
	}

	@Test
	public void testHasGenericFromClass_nested() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("nested1", NestedSelfSub.class, NestedSelfSub.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(4, retType.methods().size());
		assertEquals(NestedSelfSub.class, retType.clazz);
	}

	@Test
	public void testHasGenericFromMethod_nested() {
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("nested2", NestedSelf.class, NestedSelf.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(3, retType.methods().size());
		assertEquals(NestedSelf.class, retType.clazz);
		assertEquals(NestedSelf.class, retType.generics.get(0).clazz);
	}

	@Test
	public void testGenericSuperWildcard() {
		// 		<U extends Comparable<? super U>> NestedSelfSub orderBy(Function<String, U> sortingKeyExtractor);
		ClazzMethod addMethod = Clazz.of(NestedSelfSub.class).methods().find("orderBy", NestedSelfSub.class, Function.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(NestedSelfSub.class, retType.clazz);

		Clazz<?> paramType = addMethod.parameters().get(0).getClazz();
		assertEquals(Function.class, paramType.clazz);
		assertEquals(2, paramType.generics.size());
		assertEquals(String.class, paramType.generics.get(0).clazz);
		assertEquals(Comparable.class, paramType.generics.get(1).clazz);

		Clazz<?> compType = paramType.generics.get(1);
		assertEquals(1, compType.generics.size());
		assertEquals(Comparable.class, compType.generics.get(0).clazz);

		Clazz<?> compType2 = compType.generics.get(0);
		assertEquals(0, compType2.generics.size());
	}

	@Test
	public void testMoreSpecificFirstLevelSame1() {
		// ListHolder<List<String>> make(); ListHolder<F extends List<?>>
		ClazzMethod makeMethod = Clazz.of(ListHolderFactoryOfString.class).methods().find("make", ListHolder.class).orElseThrow(RuntimeException::new);
		assertFalse(makeMethod.hasGenericFromClass());
		assertFalse(makeMethod.hasGenericFromMethod());

		// needs to decide between ListHolder<List<String>> and ListHolder<List<Object>>
		Clazz<?> listHolderOfString = makeMethod.getReturnType();
		assertClazz(listHolderOfString, ListHolder.class, new ClazzTest.Holder(List.class, String.class));

		ClazzMethod getMethod = listHolderOfString.methods().find("get", List.class).orElseThrow(RuntimeException::new);
		assertTrue(getMethod.hasGenericFromClass());
		assertFalse(getMethod.hasGenericFromMethod());

		Clazz<?> listOfString = getMethod.getReturnType();
		assertClazz(listOfString, List.class, String.class);
	}

	@Test
	public void testMoreSpecificFirstLevelSame2() {
		// StringListHolder<? extends List<?>> make(); StringListHolder<F extends List<String>>
		ClazzMethod makeMethod = Clazz.of(StringListHolderFactoryOfWildcard.class).methods().find("make", StringListHolder.class).orElseThrow(RuntimeException::new);
		assertFalse(makeMethod.hasGenericFromClass());
		assertTrue(makeMethod.hasGenericFromMethod());

		// needs to decide between StringListHolder<List<Object>> or StringListHolder<List<String>>
		Clazz<?> stringListHolderOfWildcard = makeMethod.getReturnType();
		assertClazz(stringListHolderOfWildcard, StringListHolder.class, new ClazzTest.Holder(List.class, String.class));

		ClazzMethod getMethod = stringListHolderOfWildcard.methods().find("get", List.class).orElseThrow(RuntimeException::new);
		assertTrue(getMethod.hasGenericFromClass());
		assertFalse(getMethod.hasGenericFromMethod());

		Clazz<?> listOfWildcard = getMethod.getReturnType();
		assertClazz(listOfWildcard, List.class, String.class);
	}

	@Test
	public void testMoreSpecificEachLevelDifferent() {
		// ListHolder<? extends Collection<String>> make(); ListHolder<F extends List<?>>
		ClazzMethod addMethod = Clazz.of(StringCollectionHolderFactory.class).methods().find("make", ListHolder.class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertTrue(addMethod.hasGenericFromMethod());
		StringCollectionHolderFactory f = new StringCollectionHolderFactory();

		// needs to decide between ListHolder<List<Object>> or ListHolder<Collection<String>>
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(ListHolder.class, retType.clazz);
		assertEquals(1, retType.generics.size());

		Clazz<?> retType2 = retType.generics.get(0);
		assertEquals(List.class, retType2.clazz);
		assertEquals(1, retType2.generics.size());

		Clazz<?> retType3 = retType2.generics.get(0);
		assertEquals(String.class, retType3.clazz);
	}

	@Test
	public void testMoreSpecificDifferentBounds() {
		// ListHolder<? extends Set<String>> make(); ListHolder<F extends List<?>>
		assertThrows(IllegalArgumentException.class,
				() -> Clazz.of(StringSetHolderFactory.class).methods().find("make", ListHolder.class));
	}

	@Test
	public void testArrayOfBoundGeneric() { // todo
		// 		<T2 extends T> T2[] ArrayOfBoundGeneric()
		ClazzMethod addMethod = Clazz.of(GenericTesterImpl.class).methods().find("ArrayOfBoundGeneric", String[].class).orElseThrow(RuntimeException::new);
		assertFalse(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();


	}

	// todo next method

	@SuppressWarnings("unused")
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
		public NestedSelf<? extends NestedSelf<?>> nestedBoundWildcard(NestedSelf<? extends NestedSelf<?>> input) { return input; }
		public NestedSelfSub nested1(NestedSelfSub input) { return null; }
		public <T2 extends NestedSelf<T2>> T2 nested2(T2 input) { return null; }
		public <T2 extends T> T2[] ArrayOfBoundGeneric() { return null; }
		public List<T>[] arrayOfListOfT() { return null; }
	}

	public interface ListHolder<F extends List<?>> {
		F get();
	}

	public interface StringListHolder<F extends List<String>> {
		F get();
	}

	public static class ListHolderFactoryOfString {
		ListHolder<List<String>> make() { return null; }
	}

	public static class StringListHolderFactoryOfWildcard {
		StringListHolder<? extends List<?>> make() { return null; }
	}

	public static class StringCollectionHolderFactory {
		ListHolder<? extends Collection<String>> make() { return null; }
	}

	public static class StringSetHolderFactory {
		ListHolder<? extends Set<String>> make() { return null; }
	}

	public static class GenericTesterImpl extends GenericTester<String> {

	}

	@SuppressWarnings("unused")
	public interface NestedSelf0<K0 extends NestedSelf0<K0>> extends Consumer<K0> {
		K0 method(K0 input);
	}

	public interface NestedSelf<K extends NestedSelf<K>> extends NestedSelf0<K> {
		@Override
		K method(K input);
	}

	@SuppressWarnings("unused")
	public interface NestedSelfSub extends NestedSelf<NestedSelfSub> {
		<U extends Comparable<? super U>> NestedSelfSub orderBy(Function<String, U> sortingKeyExtractor);
	}

	public static class NestedSelfImpl<T extends NestedSelf<T>> implements NestedSelf<T> {

		@Override
		public T method(T input) {
			return null;
		}

		@Override
		public void accept(T nestedSelf) {

		}
	}

	@SuppressWarnings("unused")
	public interface ICassandraEventsQuery<P extends NestedSelf<P>> {
		P byMatchParentID(Collection<String> epcs);
	}

	@Test
	public void testICassandraEventsQuery_ReturnCase() {
		Clazz<?> c = Clazz.of(ICassandraEventsQuery.class);
		ClazzMethod addMethod = c.methods().find("byMatchParentID", NestedSelf.class, Collection.class).orElseThrow(RuntimeException::new);
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(NestedSelf.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(NestedSelf.class, retType.generics.get(0).clazz);
	}

	@SuppressWarnings("unused")
	public interface IEventService {
		ICassandraEventsQuery<?> cassandraQuery();
	}

	@Test
	public void testICassandraEventsQuery_ReturnCase_fromWildCard() {
		Clazz<?> parent = Clazz.of(IEventService.class);
		ClazzMethod pMethod = parent.methods().find("cassandraQuery", ICassandraEventsQuery.class).orElseThrow(RuntimeException::new);


		Clazz<?> c = pMethod.getReturnType();
		assertEquals(1, c.generics.size());
		assertEquals(NestedSelf.class, c.generics.get(0).clazz);

		ClazzMethod addMethod = c.methods().find("byMatchParentID", NestedSelf.class, Collection.class).orElseThrow(RuntimeException::new);
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(NestedSelf.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(NestedSelf.class, retType.generics.get(0).clazz);
	}

	@SuppressWarnings("unused")
	public interface WithStreamMethod {
		Stream<String> myStream();
	}

	@Test
	public void testStreamMethod() {
		Clazz<?> parent = Clazz.of(WithStreamMethod.class);
		ClazzMethod method = parent.methods().find("myStream", Stream.class).orElseThrow(RuntimeException::new);

		Clazz<?> c = method.getReturnType();
		assertEquals(Stream.class, c.clazz);
		assertEquals(1, c.generics.size());
		assertEquals(String.class, c.generics.get(0).clazz);

		ClazzMethod streamMethod = c.methods().find("sequential", Stream.class).orElseThrow(RuntimeException::new);
		assertTrue(streamMethod.hasGenericFromClass());
		assertFalse(streamMethod.hasGenericFromMethod());
		Clazz<?> retType = streamMethod.getReturnType();
		assertEquals(Stream.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(String.class, retType.generics.get(0).clazz);
	}

	@Test
	public void test_TwoGenericParameters() {
		Clazz<?> c = Clazz.of(BaseStream.class);
		assertEquals(BaseStream.class, c.clazz);
		assertEquals(2, c.generics.size());
		assertEquals(Object.class, c.generics.get(0).clazz);
		assertEquals(BaseStream.class, c.generics.get(1).clazz);

		ClazzMethod streamMethod = c.methods().find("sequential", BaseStream.class).orElseThrow(RuntimeException::new);
		assertTrue(streamMethod.hasGenericFromClass());
		assertFalse(streamMethod.hasGenericFromMethod());
		Clazz<?> retType = streamMethod.getReturnType();
		assertEquals(BaseStream.class, retType.clazz);
		assertEquals(2, retType.generics.size());
		assertEquals(Object.class, retType.generics.get(0).clazz);
		assertEquals(BaseStream.class, retType.generics.get(1).clazz);
		assertSame(retType, retType.generics.get(1));
	}

	@SuppressWarnings("unused")
	public static class Mike<T> {
		public void hey(List<List<List<List<T>>>> lists) {

		}
	}
}
