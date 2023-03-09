package io.ran;

import org.junit.Test;

import static io.ran.testclasses.AssertHelpers.assertMethod;
import static io.ran.testclasses.AssertHelpers.g;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ClazzMethodRegression {

	@Test
	public void test_UrlLink_nonGenericMethod() {
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		// This fails because the method isn't even included - so somehow we don't traverse parent classes well enough
		ClazzMethod someMethod = methods.find("someMethod", String.class, String.class).get();
		assertMethod(g(RedirectableLink.class), someMethod, String.class, String.class);
		assertFalse(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}

	@Test
	public void test_UrlLink_genericMethod() {
		// <T2> T mixed(T2 input)
		ClazzMethodList methods = Clazz.of(UrlLink.class).methods();
		ClazzMethod someMethod = methods.find("addParameter", UrlLink.class, String.class).get();
		// This fails because the generics are calculated incorrectly
		assertMethod(g(ParametrizableLink.class, UrlLink.class), someMethod, UrlLink.class, String.class);
		assertTrue(someMethod.hasGenericFromClass());
		assertFalse(someMethod.hasGenericFromMethod());
	}


	// Classes / Interfaces to test on
	public interface RedirectableLink {
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
	}
}
