package io.ran;

import io.ran.token.Token;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrmMapperTest {
	private TypeDescriberFactory typeDescriberFactory;
	private AutoMapper mapper;

	@Before
	public void setup() {
		 this.mapper = new AutoMapper(new DefaultRanConfig());
		 this.typeDescriberFactory = new TypeDescriberFactory(mapper);
	}

	@Test
	public void simpleField() throws Throwable {
		TypeDescriber<TestClass> describer = typeDescriberFactory.getTypeDescriber(TestClass.class);
		assertTrue(describer.fields().stream().filter(p -> "id".equals(p.getToken().snake_case())).findAny().isPresent());
	}

	@Test
	public void collectionField() throws Throwable {
		TypeDescriber<Other> describer = typeDescriberFactory.getTypeDescriber(Other.class);
		assertTrue(describer.relations().stream().filter(p -> "test_classes".equals(p.getField().snake_case())).findAny().isPresent());
	}

	@Test
	public void getValueFromProperty() throws Throwable {
		TypeDescriber<TestClass> describer = typeDescriberFactory.getTypeDescriber(TestClass.class);
		TestClass t = mapper.get(TestClass.class).newInstance();
		Mapping tMapping = (Mapping)t;
		t.setId("my id");

		Object actual = tMapping._getValue(describer.fields().get(Token.of("id")));

		assertEquals("my id", actual);
	}

	@Test
	public void keys() throws Throwable {
		TypeDescriber<TestClass> describer = typeDescriberFactory.getTypeDescriber(TestClass.class);
		assertEquals(1, describer.fields().stream().filter(p -> "other_id".equals(p.getToken().snake_case())).findAny().get().getKeys().size());
	}

	@Test
	public void fieldAnnotations() throws Throwable {
		TypeDescriber<TestClass> describer = typeDescriberFactory.getTypeDescriber(TestClass.class);
		assertEquals("muh", describer.fields().stream().filter(p -> "other_id".equals(p.getToken().snake_case())).findAny().get().getAnnotations().get(Key.class).name());
	}
}
