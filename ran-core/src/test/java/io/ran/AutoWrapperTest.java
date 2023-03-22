/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AutoWrapperTest {
	GuiceHelper.GuiceGenericFactory factory;
	private Injector injector;
	private AutoWrapper autoWrapper;

	@Before
	public void setup() {
		injector = Guice.createInjector();
		factory = new GuiceHelper.GuiceGenericFactory(new AutoMapper(), injector);
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
		wrapped.setNumbers(4, 5, 6);
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
			wrapped.getaList().add("muh");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Setter called on readonly object: AbstractCollection.addAll", e.getMessage());
		}
	}

	@Test
	public void wrapEmptyClass_withFactory() {
		Class<IMyValueInterface> wrapped = autoWrapper.wrapToClassWithFactoryInjector("TestClassName1", IMyValueInterface.class, MyTestFactory.class, "My_identifier");
		System.out.println(wrapped.getName());
		Class<IMyValueInterface> wrapped2 = autoWrapper.wrapToClassWithFactoryInjector("TestClassName2", IMyValueInterface.class, MyTestFactory.class, "My_second_identifier");
		System.out.println(wrapped2.getName());

		injector.getInstance(MyTestFactory.class).map.put("My_identifier", injector.getInstance(MyValueInstance.class).blah("ekstra"));
		injector.getInstance(MyTestFactory.class).map.put("My_second_identifier", injector.getInstance(MyValueInstance.class).blah("ekstra2"));

		assertEquals("_mjelloekstra", injector.getInstance(wrapped).hello("mjello"));
		assertEquals("_mjelloekstra2", injector.getInstance(wrapped2).hello("mjello"));
	}

	@Singleton
	public static class MyTestFactory implements AutoWrappedFactory {
		Map<String, Object> map = new HashMap<>();

		@Override
		public <T> T get(DynamicClassIdentifier identifier) {
			return (T) map.get(identifier.getIdentifier());
		}
	}

	public interface IMyValueInterface {
		String hello(String name);
	}

	public static class MyValueInstance implements IMyValueInterface {

		private final MyDep dependency;
		private String extra;

		@Inject
		public MyValueInstance(MyDep dependency) {

			this.dependency = dependency;
		}

		public String hello(String name) {
			return dependency.morphId(name) + extra;
		}

		public MyValueInstance blah(String extra) {
			this.extra = extra;
			return this;
		}
	}
}
