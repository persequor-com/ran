package io.ran;

import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

import static io.ran.testclasses.AssertHelpers.*;
import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ClazzMethodTest {

	@Test
	public void testHasGenericFromClass() {
		// T method1(T input)
		ClazzMethod method1 = Clazz.of(GenericTesterImpl.class).methods().find("method1", String.class, String.class).get();
		assertMethod(g(GenericTester.class, String.class), method1, String.class, String.class);
		assertTrue(method1.hasGenericFromClass());
		assertFalse(method1.hasGenericFromMethod());
	}

	@Test
	public void testHasGenericFromMethod() {
		// <T2> T2 method2(T2 input)
		ClazzMethod method2 = Clazz.of(GenericTesterImpl.class).methods().find("method2", Object.class, Object.class).get();
		assertMethod(g(GenericTester.class, String.class), method2, Object.class, Object.class);
		assertFalse(method2.hasGenericFromClass());
		assertTrue(method2.hasGenericFromMethod());
	}

	@Test
	public void testHasBothGenericFromMethodAndClass() {
		// <T2> T mixed(T2 input)
		ClazzMethod mixed = Clazz.of(GenericTesterImpl.class).methods().find("mixed", String.class, Object.class).get();
		assertMethod(g(GenericTester.class, String.class), mixed, String.class, Object.class);
		assertTrue(mixed.hasGenericFromClass());
		assertTrue(mixed.hasGenericFromMethod());
	}

	@Test
	public void testHasWildCard() {
		// List<?> wildcard(String input)
		ClazzMethod wildcard = Clazz.of(GenericTesterImpl.class).methods().find("wildcard", List.class, String.class).get();
		assertMethod(g(GenericTester.class, String.class), wildcard, g(List.class, Object.class), String.class);
		assertFalse(wildcard.hasGenericFromClass());
		assertTrue(wildcard.hasGenericFromMethod());
	}

	@Test
	public void testHasNeitherGenericFromMethodNorClass() {
		// String method3(String input)
		ClazzMethod method3 = Clazz.of(GenericTesterImpl.class).methods().find("method3", String.class, String.class).get();
		assertMethod(g(GenericTester.class, String.class), method3, String.class, String.class);
		assertFalse(method3.hasGenericFromClass());
		assertFalse(method3.hasGenericFromMethod());
	}

	@Test
	public void testHasGenericFromClass_parameterized() {
		// List<T> param1(List<T> input)
		ClazzMethod param1 = Clazz.of(GenericTesterImpl.class).methods().find("param1", List.class, List.class).get();
		assertMethod(g(GenericTester.class, String.class), param1, g(List.class, String.class), g(List.class, String.class));
		assertTrue(param1.hasGenericFromClass());
		assertFalse(param1.hasGenericFromMethod());
	}

	@Test
	public void testHasGenericFromMethod_parameterized() {
		// <T2> List<T2> param2(List<T2> input)
		ClazzMethod param2 = Clazz.of(GenericTesterImpl.class).methods().find("param2", List.class, List.class).get();
		assertMethod(g(GenericTester.class, String.class), param2, g(List.class, Object.class), g(List.class, Object.class));
		assertFalse(param2.hasGenericFromClass());
		assertTrue(param2.hasGenericFromMethod());
	}

	@Test
	public void testParamNonGeneric() {
		// List<String> paramNonGeneric(List<String> input)
		ClazzMethod paramNonGeneric = Clazz.of(GenericTesterImpl.class).methods().find("paramNonGeneric", List.class, List.class).get();
		assertMethod(g(GenericTester.class, String.class), paramNonGeneric, g(List.class, String.class), g(List.class, String.class));
		assertFalse(paramNonGeneric.hasGenericFromClass());
		assertFalse(paramNonGeneric.hasGenericFromMethod());
	}

	@Test
	public void testHasGenericFromBothMethodAndClass_parameterized() {
		// <T2> List<T> mixed2(List<T2> input)
		ClazzMethod mixed2 = Clazz.of(GenericTesterImpl.class).methods().find("mixed2", List.class, List.class).get();
		assertMethod(g(GenericTester.class, String.class), mixed2, g(List.class, String.class), g(List.class, Object.class));
		assertTrue(mixed2.hasGenericFromClass());
		assertTrue(mixed2.hasGenericFromMethod());
	}

	@Test
	public void testHasBothGenericFromMethodAndClass_parameterizedWithClosure() {
		// <T2 extends T> List<T2> mixed3(List<T2> input)
		ClazzMethod mixed3 = Clazz.of(GenericTesterImpl.class).methods().find("mixed3", List.class, List.class).get();
		assertMethod(g(GenericTester.class, String.class), mixed3, g(List.class, String.class), g(List.class, String.class));
		assertFalse(mixed3.hasGenericFromClass());
		assertTrue(mixed3.hasGenericFromMethod());
	}

	@Test
	public void testHasWildCard_nested() {
		// NestedSelf<?> nestedWildcard(NestedSelf<?> input)
		ClazzMethod nestedWildcard = Clazz.of(GenericTesterImpl.class).methods().find("nestedWildcard", NestedSelf.class, NestedSelf.class).get();
		assertMethod(g(GenericTester.class, String.class), nestedWildcard, g(NestedSelf.class, self()), g(NestedSelf.class, self()));
		assertFalse(nestedWildcard.hasGenericFromClass());
		assertTrue(nestedWildcard.hasGenericFromMethod());
	}

	@Test
	public void testHasBoundWildCard_nested() throws InvocationTargetException, IllegalAccessException {
		// NestedSelf<? extends NestedSelf<?>> nestedBoundWildcard(NestedSelf<? extends NestedSelf<?>> input)
		ClazzMethod nestedBoundWildcard = Clazz.of(GenericTesterImpl.class).methods().find("nestedBoundWildcard", NestedSelf.class, NestedSelf.class).get();
		assertMethod(g(GenericTester.class, String.class), nestedBoundWildcard, g(NestedSelf.class, self()), g(NestedSelf.class, self()));
		assertFalse(nestedBoundWildcard.hasGenericFromClass());
		assertTrue(nestedBoundWildcard.hasGenericFromMethod());

		// Try calling the method, to verify that the types actually works at runtime
		NestedSelf<?> ns = new NestedSelfImpl<>();
		Object actual = nestedBoundWildcard.getMethod().invoke(new GenericTesterImpl(), ns);
		assertSame(ns, actual);
	}

	@Test
	public void testHasGenericFromClass_nested() {
		// NestedSelfSub nested1(NestedSelfSub input)
		ClazzMethod nested1 = Clazz.of(GenericTesterImpl.class).methods().find("nested1", NestedSelfSub.class, NestedSelfSub.class).get();
		assertMethod(g(GenericTester.class, String.class), nested1, NestedSelfSub.class, NestedSelfSub.class);
		assertFalse(nested1.hasGenericFromClass());
		assertFalse(nested1.hasGenericFromMethod());
		assertEquals(4, nested1.getReturnType().methods().size());
	}

	@Test
	public void testHasGenericFromMethod_nested() {
		// <T2 extends NestedSelf<T2>> T2 nested2(T2 input)
		ClazzMethod nested2 = Clazz.of(GenericTesterImpl.class).methods().find("nested2", NestedSelf.class, NestedSelf.class).get();
		assertMethod(g(GenericTester.class, String.class), nested2, g(NestedSelf.class, self()), g(NestedSelf.class, self()));
		assertFalse(nested2.hasGenericFromClass());
		assertTrue(nested2.hasGenericFromMethod());
		assertEquals(3, nested2.getReturnType().methods().size());
	}

	@Test
	public void testArrayOfGeneric() {
		// T[] arrayOfGeneric()
		ClazzMethod arrayOfGeneric = Clazz.of(GenericTesterImpl.class).methods().find("arrayOfGeneric", String[].class).get();
		assertMethod(g(GenericTester.class, String.class), arrayOfGeneric, String[].class);
		assertTrue(arrayOfGeneric.hasGenericFromClass());
		assertFalse(arrayOfGeneric.hasGenericFromMethod());
	}

	@Test
	public void testArrayOfBoundGeneric() {
		// <T2 extends T> T2[] arrayOfBoundGeneric()
		ClazzMethod arrayOfBoundGeneric = Clazz.of(GenericTesterImpl.class).methods().find("arrayOfBoundGeneric", String[].class).get();
		assertMethod(g(GenericTester.class, String.class), arrayOfBoundGeneric, String[].class);
		assertFalse(arrayOfBoundGeneric.hasGenericFromClass());
		assertTrue(arrayOfBoundGeneric.hasGenericFromMethod());
		Clazz<?> arr = arrayOfBoundGeneric.getReturnType().getComponentType();
		assertClazz(arr, String.class);
	}

	@Test
	public void testArrayOfListOfT() {
		// List<T>[] arrayOfListOfT()
		ClazzMethod arrayOfListOfT = Clazz.of(GenericTesterImpl.class).methods().find("arrayOfListOfT", List[].class).get();
		assertMethod(g(GenericTester.class, String.class), arrayOfListOfT, g(List[].class, String.class));
		assertTrue(arrayOfListOfT.hasGenericFromClass());
		assertFalse(arrayOfListOfT.hasGenericFromMethod());
	}

	@Test
	public void arrayOfListOfWildT() {
		// List<? extends T>[] arrayOfListOfWildT()
		ClazzMethod arrayOfListOfWildT = Clazz.of(GenericTesterImpl.class).methods().find("arrayOfListOfWildT", List[].class).get();
		assertMethod(g(GenericTester.class, String.class), arrayOfListOfWildT, g(List[].class, String.class));
		assertFalse(arrayOfListOfWildT.hasGenericFromClass());
		assertTrue(arrayOfListOfWildT.hasGenericFromMethod());
	}

	@Test
	public void testGenericSuperWildcard() {
		// <U extends Comparable<? super U>> NestedSelfSub orderBy(Function<String, U> sortingKeyExtractor)
		ClazzMethod orderBy = Clazz.of(NestedSelfSub.class).methods().find("orderBy", NestedSelfSub.class, Function.class).get();
		assertMethod(NestedSelfSub.class, orderBy,
				/* return type */   NestedSelfSub.class,
				/* arg 0 */            g(Function.class,
						String.class,
						g(Comparable.class,
								Object.class)));
		assertFalse(orderBy.hasGenericFromClass());
		assertTrue(orderBy.hasGenericFromMethod());
	}


	@Test
	public void testICassandraEventsQuery_ReturnCase() {
		Clazz<?> c = Clazz.of(ICassandraEventsQuery.class);
		ClazzMethod addMethod = c.methods().find("byMatchParentID", NestedSelf.class, Collection.class).get();
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(NestedSelf.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(NestedSelf.class, retType.generics.get(0).clazz);
	}

	@Test
	public void testICassandraEventsQuery_ReturnCase_fromWildCard() {
		Clazz<?> parent = Clazz.of(IEventService.class);
		ClazzMethod pMethod = parent.methods().find("cassandraQuery", ICassandraEventsQuery.class).get();


		Clazz<?> c = pMethod.getReturnType();
		assertEquals(1, c.generics.size());
		assertEquals(NestedSelf.class, c.generics.get(0).clazz);

		ClazzMethod addMethod = c.methods().find("byMatchParentID", NestedSelf.class, Collection.class).get();
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		Clazz<?> retType = addMethod.getReturnType();
		assertEquals(NestedSelf.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(NestedSelf.class, retType.generics.get(0).clazz);
	}

	@Test
	public void testStreamMethod() {
		Clazz<?> parent = Clazz.of(WithStreamMethod.class);
		ClazzMethod method = parent.methods().find("myStream", Stream.class).get();

		Clazz<?> c = method.getReturnType();
		assertEquals(Stream.class, c.clazz);
		assertEquals(1, c.generics.size());
		assertEquals(String.class, c.generics.get(0).clazz);

		ClazzMethod streamMethod = c.methods().find("sequential", Stream.class).get();
		assertTrue(streamMethod.hasGenericFromClass());
		assertFalse(streamMethod.hasGenericFromMethod());
		Clazz<?> retType = streamMethod.getReturnType();
		assertEquals(Stream.class, retType.clazz);
		assertEquals(1, retType.generics.size());
		assertEquals(String.class, retType.generics.get(0).clazz);
	}

	@Test
	public void test_TwoGenericParameters() {
		Clazz<?> baseStream = Clazz.of(BaseStream.class);
		assertClazz(baseStream, BaseStream.class, Object.class, self());

		ClazzMethod streamMethod = baseStream.methods().find("sequential", BaseStream.class).get();
		assertMethod(baseStream, streamMethod, g(BaseStream.class, Object.class, self()));
		assertTrue(streamMethod.hasGenericFromClass());
		assertFalse(streamMethod.hasGenericFromMethod());
	}

	// coincidentally works, but swapping the order breaks it
	@Test
	public void testMoreSpecificFirstLevelSame1() {
		// ListHolder<List<String>> make(); ListHolder<F extends List<?>>
		// needs to decide between ListHolder<List<String>> and ListHolder<List<Object>>
		ClazzMethod makeMethod = Clazz.of(ListHolderFactoryOfString.class).methods().find("make", ListHolder.class).get();
		assertMethod(ListHolderFactoryOfString.class, makeMethod, g(ListHolder.class, g(List.class, String.class)));
		assertFalse(makeMethod.hasGenericFromClass());
		assertFalse(makeMethod.hasGenericFromMethod());

		ClazzMethod getMethod = makeMethod.getReturnType().methods().find("get", List.class).get();
		assertMethod(g(ListHolder.class, g(List.class, String.class)), getMethod, g(List.class, String.class));
		assertTrue(getMethod.hasGenericFromClass());
		assertFalse(getMethod.hasGenericFromMethod());
	}

	@Ignore("not supported atm")
	@Test
	public void testMoreSpecificFirstLevelSame2() {
		// StringListHolder<? extends List<?>> make(); StringListHolder<F extends List<String>>
		// needs to decide between StringListHolder<List<Object>> or StringListHolder<List<String>>
		ClazzMethod makeMethod = Clazz.of(StringListHolderFactoryOfWildcard.class).methods().find("make", StringListHolder.class).get();
		assertMethod(StringListHolderFactoryOfWildcard.class, makeMethod, g(StringListHolder.class, g(List.class, String.class)));
		assertFalse(makeMethod.hasGenericFromClass());
		assertTrue(makeMethod.hasGenericFromMethod());

		ClazzMethod getMethod = makeMethod.getReturnType().methods().find("get", List.class).get();
		assertMethod(StringListHolder.class, getMethod, g(List.class, String.class));
		assertTrue(getMethod.hasGenericFromClass());
		assertFalse(getMethod.hasGenericFromMethod());
	}

	@Ignore("not supported atm")
	@Test
	public void testMoreSpecificEachLevelDifferent() {
		// ListHolder<? extends Collection<String>> make(); ListHolder<F extends List<?>>
		// needs to decide between ListHolder<List<Object>> or ListHolder<Collection<String>>
		ClazzMethod makeMethod = Clazz.of(StringCollectionHolderFactory.class).methods().find("make", ListHolder.class).get();
		assertMethod(StringCollectionHolderFactory.class, makeMethod, g(ListHolder.class, g(List.class, String.class)));
		assertFalse(makeMethod.hasGenericFromClass());
		assertTrue(makeMethod.hasGenericFromMethod());

		ClazzMethod getMethod = makeMethod.getReturnType().methods().find("get", List.class).get();
		assertMethod(ListHolder.class, getMethod, g(List.class, String.class));
		assertTrue(getMethod.hasGenericFromClass());
		assertFalse(getMethod.hasGenericFromMethod());
	}

	@Test
	public void testMoreSpecificDifferentBounds() {
		// ListHolder<? extends Set<String>> make(); ListHolder<F extends List<?>>
		// this is effectively multiple bounds, not supported
		assertThrows(IllegalArgumentException.class,
				() -> Clazz.of(StringSetHolderFactory.class).methods().find("make", ListHolder.class));
	}


	@Test
	public void testDeepGenerics() {
		// void listOff(List<List<List<List<T>>>> lists)
		Clazz<?> deepGenerics = Clazz.ofClasses(DeepGenerics.class, String.class);
		ClazzMethod listOff = deepGenerics.methods().find("listOff", Void.TYPE, List.class).get();
		assertMethod(g(DeepGenerics.class, String.class), listOff, Void.TYPE, g(List.class, g(List.class, g(List.class, g(List.class, String.class)))));
		assertTrue(listOff.hasGenericFromClass());
		assertFalse(listOff.hasGenericFromMethod());
	}

	@Test
	public void testDeepArray() {
		// void arrayOff(T[][][][] arr)
		Clazz<?> deepGenerics = Clazz.ofClasses(DeepGenerics.class, String.class);
		ClazzMethod listOff = deepGenerics.methods().find("arrayOff", Void.TYPE, String[][][][].class).get();
		assertMethod(g(DeepGenerics.class, String.class), listOff, Void.TYPE, String[][][][].class);
		assertTrue(listOff.hasGenericFromClass());
		assertFalse(listOff.hasGenericFromMethod());
	}

	@Test
	public void testDeepArrayGenerics() {
		// void allOff(List<List<List<T[][]>[][][]>>[] allaaalaat
		Clazz<?> deepGenerics = Clazz.ofClasses(DeepGenerics.class, String.class);
		ClazzMethod allOff = deepGenerics.methods().find("allOff", Void.TYPE, List[].class).get();
		assertMethod(g(DeepGenerics.class, String.class), allOff, Void.TYPE, g(List[].class, g(List.class, g(List[][][].class, String[][].class))));
		assertTrue(allOff.hasGenericFromClass());
		assertFalse(allOff.hasGenericFromMethod());
	}

	@SuppressWarnings("unused")
	public static class GenericTester<T> {
		public T method1(T input) {
			return null;
		}

		public <T2> T2 method2(T2 input) {
			return null;
		}

		public <T2> T mixed(T2 input) {
			return null;
		}

		public List<?> wildcard(String input) {
			return null;
		}

		public String method3(String input) {
			return null;
		}

		public List<T> param1(List<T> input) {
			return null;
		}

		public <T2> List<T2> param2(List<T2> input) {
			return null;
		}

		public List<String> paramNonGeneric(List<String> input) {
			return null;
		}

		public <T2> List<T> mixed2(List<T2> input) {
			return null;
		}

		public <T2 extends T> List<T2> mixed3(List<T2> input) {
			return null;
		}

		public NestedSelf<?> nestedWildcard(NestedSelf<?> input) {
			return null;
		}

		public NestedSelf<? extends NestedSelf<?>> nestedBoundWildcard(NestedSelf<? extends NestedSelf<?>> input) {
			return input;
		}

		public NestedSelfSub nested1(NestedSelfSub input) {
			return null;
		}

		public <T2 extends NestedSelf<T2>> T2 nested2(T2 input) {
			return null;
		}

		public T[] arrayOfGeneric() {
			return null;
		}

		public <T2 extends T> T2[] arrayOfBoundGeneric() {
			return null;
		}

		public List<T>[] arrayOfListOfT() {
			return null;
		}

		public List<? extends T>[] arrayOfListOfWildT() {
			return null;
		}
	}

	public static class GenericTesterImpl extends GenericTester<String> {

	}

	public interface NestedSelf0<K0 extends NestedSelf0<K0>> extends Consumer<K0> {
		@SuppressWarnings("unused")
		K0 method(K0 input);
	}

	public interface NestedSelf<K extends NestedSelf<K>> extends NestedSelf0<K> {
		@Override
		K method(K input);
	}

	public interface NestedSelfSub extends NestedSelf<NestedSelfSub> {
		@SuppressWarnings("unused")
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

	public interface ICassandraEventsQuery<P extends NestedSelf<P>> {
		@SuppressWarnings("unused")
		P byMatchParentID(Collection<String> epcs);
	}

	public interface IEventService {
		@SuppressWarnings("unused")
		ICassandraEventsQuery<?> cassandraQuery();
	}

	@SuppressWarnings("unused")
	public interface WithStreamMethod {
		Stream<String> myStream();
	}

	public interface ListHolder<F extends List<?>> {
		F get();
	}

	public interface StringListHolder<F extends List<String>> {
		F get();
	}

	public static class ListHolderFactoryOfString {
		@SuppressWarnings("unused")
		ListHolder<List<String>> make() {
			return null;
		}
	}

	public static class StringListHolderFactoryOfWildcard {
		@SuppressWarnings("unused")
		StringListHolder<? extends List<?>> make() {
			return null;
		}
	}

	public static class StringCollectionHolderFactory {
		@SuppressWarnings("unused")
		ListHolder<? extends Collection<String>> make() {
			return null;
		}
	}

	public static class StringSetHolderFactory {
		@SuppressWarnings("unused")
		ListHolder<? extends Set<String>> make() {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public static class DeepGenerics<T> {
		public void listOff(List<List<List<List<T>>>> lists) {
		}

		public void arrayOff(T[][][][] arr) {
		}

		public void allOff(List<List<List<T[][]>[][][]>>[] allaaalaat) {
		}
	}
}
