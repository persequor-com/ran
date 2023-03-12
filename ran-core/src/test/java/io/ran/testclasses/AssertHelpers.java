package io.ran.testclasses;

import io.ran.Clazz;
import io.ran.ClazzMethod;
import io.ran.ClazzMethodParameter;

import java.util.List;

import static org.junit.Assert.*;

public class AssertHelpers {
	public static void assertClazz(Clazz<?> clazz, Class<?> type, Object... generics) {
		assertSame(type, clazz.clazz);
		assertEquals(generics.length, clazz.generics.size());

		if (type.isArray()) {
			assertTrue(clazz.isArray());
			assertClazz(clazz.getComponentType(), type.getComponentType(), generics);
		} else {
			assertEquals(clazz.clazz.getTypeParameters().length, generics.length);
		}

		int i = 0;
		try {
			for (; i < generics.length; i++) {
				Object expected = generics[i];
				Clazz<?> actual = clazz.generics.get(i);
				if (expected == self()) {
					if (actual.generics.isEmpty()) {
						assertSame(clazz.clazz, actual.clazz);
					} else {
						assertClazz(actual, clazz.clazz, generics);
					}
				} else {
					innerAssert(actual, expected);
				}
			}
		} catch (AssertionError e) {
			throw new AssertionError("Type mismatch for type " + i + " of " + clazz, e);
		}
	}

	public static void assertMethod(Object declaringClass, ClazzMethod method, Object returnType, Object... paramTypes) {
		List<ClazzMethodParameter> params = method.parameters();
		assertEquals(paramTypes.length, params.size());
		innerAssert(method.getDeclaringClazz(), declaringClass);
		innerAssert(method.getReturnType(), returnType);
		int i = 0;
		try {
			for (; i < params.size(); i++) {
				innerAssert(params.get(i).getClazz(), paramTypes[i]);
			}
		} catch (AssertionError e) {
			throw new AssertionError("Type mismatch for parameter " + i + " of " + method, e);
		}
	}

	private static void innerAssert(Clazz<?> actual, Object expected) {
		if (expected instanceof Clazz) {
			assertSame(expected, actual);
		} else if (expected instanceof Class) {
			assertClazz(actual, (Class<?>) expected);
		} else if (expected instanceof Holder) {
			Holder holder = (Holder) expected;
			if (holder.isRaw()) {
				assertSame(holder.type, actual.clazz);
				assertEquals(0, actual.generics.size());
			} else {
				assertClazz(actual, holder.type, holder.generics);
			}
		} else {
			fail("unknown type " + expected.getClass());
		}
	}

	// matches either same check as parent has, or just a raw class of parent, to break the loop
	public static Holder self() {
		return Holder.self;
	}

	public static Holder g(Class<?> type, Object... generics) {
		return new Holder(type, generics);
	}

	public static class Holder {
		public static final Holder self = new Holder(null);
		private final Class<?> type;
		private final Object[] generics;
		private Holder(Class<?> type) {
			this.type = type;
			this.generics = new Object[0];
		}
		private Holder(Class<?> type, Object... generics) {
			if (generics.length == 0) {
				throw new IllegalArgumentException("Can't use g() without generics. Use g(A.class, B.class) to define A<B>, or r(A.class) to define A");
			}
			this.type = type;
			this.generics = generics;
		}
		boolean isRaw() {
			return generics.length == 0;
		}
	}

	public static Holder r(Class<?> rawType) {
		return new Holder(rawType);
	}
}
