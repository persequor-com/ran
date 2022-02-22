package io.ran;


import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InjectionTest {
	GuiceHelper helper;

	@Before
	public void setup() {
		helper = new GuiceHelper();
	}

	@Test
	public void constructorInjection() {
		WithConstructorInjection actual = helper.factory.get(WithConstructorInjection.class);
		actual.setId("myid");

		assertEquals("_myid", actual.morphed());
	}

	@Test
	public void constructorInjection_whichIsCalledInTheConstructor() {
		WithDelegatedConstructorInjection actual = helper.factory.get(WithDelegatedConstructorInjection.class);
		actual.setId("myid");

		assertEquals("_myid", actual.morphed());
	}

	@Test
	public void methodInjection() {
		WithMethodInjection actual = helper.factory.get(WithMethodInjection.class);
		actual.setId("myid");

		assertEquals("_myid", actual.morphed());
	}
}
