package io.ran;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AutoWrapperTest {
	GuiceHelper.GuiceGenericFactory factory;
	private Injector injector;
	private AutoMapper autoMapper;
	private AutoWrapper autoWrapper;

	@Before
	public void setup() {
		injector = Guice.createInjector();
		autoMapper = new AutoMapper();
		factory = new GuiceHelper.GuiceGenericFactory(autoMapper, injector);
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
	public void wrapEmptyClass_withFactory() {
		Class<IMyValueInterface> wrapped = autoWrapper.wrapToClassWithFactoryInjector("TestClassName1", IMyValueInterface.class, MyTestFactory.class, "My identifier");
		System.out.println(wrapped.getName());
		Class<IMyValueInterface> wrapped2 = autoWrapper.wrapToClassWithFactoryInjector("TestClassName2", IMyValueInterface.class, MyTestFactory.class, "My second identifier");
		System.out.println(wrapped2.getName());

		injector.getInstance(MyTestFactory.class).map.put("My identifier", injector.getInstance(MyValueInstance.class).blah("ekstra"));
		injector.getInstance(MyTestFactory.class).map.put("My second identifier", injector.getInstance(MyValueInstance.class).blah("ekstra2"));

		assertEquals("_mjelloekstra", injector.getInstance(wrapped).hello("mjello"));
		assertEquals("_mjelloekstra2", injector.getInstance(wrapped2).hello("mjello"));
	}

	@Singleton
	public static class MyTestFactory implements AutoWrappedFactory {
		Map<String, Object> map = new HashMap<>();

		@Override
		public <T> T get(DynamicClassIdentifier identifier) {
			return (T) map.get(identifier.get());
		}
	}

	public interface IMyValueInterface {
		String hello(String name);
	}

	public static class MyValueInstance implements IMyValueInterface {

		private MyDep dependency;
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
