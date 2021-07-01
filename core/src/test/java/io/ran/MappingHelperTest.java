package io.ran;

import io.ran.testclasses.Car;
import io.ran.testclasses.ObjectMap;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MappingHelperTest {
	private GuiceHelper helper;
	private MappingHelper mappingHelper;

	@Before
	public void setup() {
		helper = new GuiceHelper();
		mappingHelper = new MappingHelper(helper.factory);
	}

	@Test
	public void happy() {
		Car car = new Car();
		car.setId("my id");
		ObjectMap map = new ObjectMap();
		mappingHelper.columnize(car, map);

		assertEquals("my id", map.getString(Token.of("id")));
	}
}