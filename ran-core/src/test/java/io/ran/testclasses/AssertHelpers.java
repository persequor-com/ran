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
		assertEquals(clazz.clazz.getTypeParameters().length, generics.length);
		int i = 0;
		try {
			for (; i < generics.length; i++) {
				Object expected = generics[i];
				Clazz<?> actual = clazz.generics.get(i);
				if (expected == self()) {
					assertSame(clazz, actual);
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
			throw new AssertionError("Type mismatch for parameter " + i + " of " + method);
		}
	}

	private static void innerAssert(Clazz<?> actual, Object expected) {
		if (expected == null) {
			return; // null matches anything
		}
		if (expected instanceof Clazz) {
			assertSame(expected, actual);
		} else if (expected instanceof Class) {
			assertClazz(actual, (Class<?>) expected);
		} else if (expected instanceof Holder) {
			Holder holder = (Holder) expected;
			assertClazz(actual, holder.type, holder.generics);
		} else {
			fail("unknown type " + expected.getClass());
		}
	}

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
		private Holder(Class<?> type, Object... generics) {
			this.type = type;
			this.generics = generics;
		}
	}
}
