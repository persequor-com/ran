package io.ran;

import org.junit.Test;

import static io.ran.testclasses.AssertHelpers.assertMethod;
import static io.ran.testclasses.AssertHelpers.g;
import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ClazzMethodRegression {

	@Test
	public void test_UrlLink_nonGenericMethodBase() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		// This fails because the method isn't even included - so somehow we don't traverse parent classes well enough
		ClazzMethod someMethod = methods.find("someMethodBase", String.class, String.class).get();
		assertMethod(UrlLink.class, someMethod, String.class, String.class);
		assertFalse(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_nonGenericMethodBase2() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		// This fails because the method isn't even included - so somehow we don't traverse parent classes well enough
		ClazzMethod someMethod = methods.find("someMethodBase2", String.class, String.class).get();
		assertMethod(RedirectableLinkBase2.class, someMethod, String.class, String.class);
		assertFalse(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_nonGenericMethod() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		// This fails because the method isn't even included - so somehow we don't traverse parent classes well enough
		ClazzMethod someMethod = methods.find("someMethod", String.class, String.class).get();
		assertMethod(RedirectableLink.class, someMethod, String.class, String.class);
		assertFalse(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_genericMethod() {
		// <T2> T mixed(T2 input)
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		ClazzMethod someMethod = methods.find("addParameter", UrlLink.class, String.class).get();
		assertEquals(UrlLink.class, someMethod.getReturnType().clazz);
		// This fails because the generics are calculated incorrectly
		assertMethod(g(ParametrizableLink.class, UrlLink.class), someMethod, UrlLink.class, String.class);
		assertTrue(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_genericMethod2() {
		// <T2> T mixed(T2 input)
		ClazzMethodList methods = Clazz.of(ParametrizableLink.class).methods();
		ClazzMethod someMethod = methods.find("addParameter", ParametrizableLink.class, String.class).get();
		assertEquals(ParametrizableLink.class, someMethod.getReturnType().clazz);
		// This fails because the generics are calculated incorrectly
		assertMethod(g(ParametrizableLink.class, ParametrizableLink.class), someMethod, ParametrizableLink.class, String.class);
		assertTrue(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}


	// Classes / Interfaces to test on
	public interface RedirectableLinkBase {
		default String someMethodBase(String input) { return "HelloBase "+input; }
	}

	public interface RedirectableLinkBase2 {
		default String someMethodBase2(String input) { return "HelloBase "+input; }
	}

	public interface RedirectableLink extends RedirectableLinkBase, RedirectableLinkBase2{
		default String someMethod(String input) { return "Hello "+input; }
	}

	public static abstract class ExtensionLink {
		protected void setType(String type) {
			/* nothing */
		}
	}
	public static abstract class ParametrizableLink<T extends ParametrizableLink> extends ExtensionLink {
		public T addParameter(String name) {
			//parameters.put(name, value);
			return (T) this;
		}
	}
	public static class UrlLink extends ParametrizableLink<UrlLink> implements RedirectableLink {
		public String someMethod2(String input) { return "Hello2 "+input; }

		@Override
		public String someMethodBase(String input) {
			return RedirectableLink.super.someMethodBase(input)+" plus something";
		}
	}
}
