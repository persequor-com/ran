package io.ran;

import org.junit.Test;

import static io.ran.testclasses.AssertHelpers.*;
import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ClazzMethodRegressionTest {

	@Test
	public void test_UrlLink_nonGenericMethodBase() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		ClazzMethod someMethodBase = methods.find("someMethodBase", String.class, String.class).get();
		assertMethod(UrlLink.class, someMethodBase, String.class, String.class);
		assertFalse(someMethodBase.hasGenericFromClass());
		assertFalse(someMethodBase.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_nonGenericMethodBase2() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		ClazzMethod someMethodBase2 = methods.find("someMethodBase2", String.class, String.class).get();
		assertMethod(RedirectableLinkBase2.class, someMethodBase2, String.class, String.class);
		assertFalse(someMethodBase2.hasGenericFromClass());
		assertFalse(someMethodBase2.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_nonGenericMethod() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		ClazzMethod someMethod = methods.find("someMethod", String.class, String.class).get();
		assertMethod(RedirectableLink.class, someMethod, String.class, String.class);
		assertFalse(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_genericMethod() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		ClazzMethod addParameter = methods.find("addParameter", UrlLink.class, String.class).get();
		assertMethod(g(ParametrizableLink.class, UrlLink.class), addParameter, UrlLink.class, String.class);
		assertTrue(addParameter.hasGenericFromClass());
		assertFalse(addParameter.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_genericMethod2() {
		ClazzMethodList methods = Clazz.of(ParametrizableLink.class).methods();
		ClazzMethod addParameter = methods.find("addParameter", ParametrizableLink.class, String.class).get();
		assertMethod(g(ParametrizableLink.class, self()), addParameter, r(ParametrizableLink.class), String.class);
		assertTrue(addParameter.hasGenericFromClass());
		assertFalse(addParameter.hasGenericFromMethod());
	}


	// Classes / Interfaces to test on
	public interface RedirectableLinkBase {
		default String someMethodBase(String input) {
			return "HelloBase " + input;
		}
	}

	public interface RedirectableLinkBase2 {
		@SuppressWarnings("unused")
		default String someMethodBase2(String input) {
			return "HelloBase " + input;
		}
	}

	public interface RedirectableLink extends RedirectableLinkBase, RedirectableLinkBase2 {
		@SuppressWarnings("unused")
		default String someMethod(String input) {
			return "Hello " + input;
		}
	}

	public static abstract class ExtensionLink {
		@SuppressWarnings("unused")
		protected void setType(String type) {
			/* nothing */
		}
	}

	@SuppressWarnings("rawtypes")
	public static abstract class ParametrizableLink<T extends ParametrizableLink> extends ExtensionLink {
		@SuppressWarnings("unused")
		public T addParameter(String name) {
			//parameters.put(name, value);
			//noinspection unchecked
			return (T) this;
		}
	}

	public static class UrlLink extends ParametrizableLink<UrlLink> implements RedirectableLink {
		@SuppressWarnings("unused")
		public String someMethod2(String input) {
			return "Hello2 " + input;
		}

		@Override
		public String someMethodBase(String input) {
			return RedirectableLink.super.someMethodBase(input) + " plus something";
		}
	}
}
