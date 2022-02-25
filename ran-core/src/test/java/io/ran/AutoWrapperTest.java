package io.ran;

import com.google.inject.Guice;
import com.google.inject.Injector;
import jdk.internal.dynalink.linker.LinkerServices;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AutoWrapperTest {
	GenericFactory factory;
	private Injector injector;
	private AutoMapper autoMapper;
	private AutoWrapper autoWrapper;

	@Before
	public void setup() {
		injector = Guice.createInjector(binder -> binder.bind(RanConfig.class).toInstance(new RanConfig() {
			@Override
			public boolean enableRanClassesDebugging() {
				return false;
			}

			@Override
			public String projectBasePath() {
				return null;
			}
		}));
		autoMapper = new AutoMapper(injector.getInstance(RanConfig.class));
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
}
