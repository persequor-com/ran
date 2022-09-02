package io.ran;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class AutoWrapperTest {
	GenericFactory factory;
	private Injector injector;
	private AutoMapper autoMapper;
	private AutoWrapper autoWrapper;
	private ReadonlyWrapper readonlyWrapper;

	@Before
	public void setup() {
		injector = Guice.createInjector();
		autoMapper = new AutoMapper();
		factory = new GuiceHelper.GuiceGenericFactory(autoMapper,injector);
		autoWrapper = new AutoWrapper(factory);
	}

	@Test
	public void wrapEmptyClass() {
		EmptyClass instance = new EmptyClass();
		EmptyClassWrapper wrapped = autoWrapper.wrap(EmptyClassWrapper.class, instance);
		assertEquals("empty", wrapped.toString());
	}

	@Test
	public void wrapPojoWithAllTypes() {
		TestClass instance = new TestClass();
		instance.setaShort((short) 1);
		instance.setInteger(2);
		instance.setaLong(3);
		instance.setId("muh");
		TestClassWrapper wrapped = autoWrapper.wrap(TestClassWrapper.class, instance);
		assertEquals("muh1-2-3", wrapped.toString());
	}

	@Test
	public void modifyingWrapee() {
		TestClass instance = new TestClass();
		instance.setaShort((short) 1);
		instance.setInteger(2);
		instance.setaLong(3);
		instance.setId("muh");
		TestClassWrapper wrapped = autoWrapper.wrap(TestClassWrapper.class, instance);
		wrapped.setNumbers(4,5,6);
		assertEquals("muh4-5-6", wrapped.toString());
		assertEquals(4, instance.getaShort());
		assertEquals(5, instance.getInteger());
		assertEquals(6, instance.getaLong());
	}

	@Test
	public void readonlyWrapper() {
		TestClass instance = new TestClass();
		instance.setaShort((short) 1);
		instance.setaList(new ArrayList<>());
		instance.getaList().add("moo");
		instance.getaList().add("quack");
		TestClass wrapped = ReadonlyWrapper.readonlyWrap(TestClass.class, instance);

		try {
			wrapped.setaShort((short) 2);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Setter called on readonly object: TestClass.setaShort", e.getMessage());
		}

		assertTrue(wrapped.getaList() instanceof Wrappee);


		try {
			wrapped.getaList().add("muh");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Setter called on readonly object: AbstractList.add", e.getMessage());
		}

		try {
			wrapped.getaList().addAll(Collections.singleton("muh"));
			fail();
		} catch (RuntimeException e) {
			assertEquals("Setter called on readonly object: AbstractCollection.addAll", e.getMessage());
		}
	}
}
