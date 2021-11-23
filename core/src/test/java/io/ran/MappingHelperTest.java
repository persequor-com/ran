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
	public void happy_columnize() {
		Car car = helper.factory.get(Car.class);
		car.setId("my id");
		ObjectMap map = new ObjectMap();
		mappingHelper.columnize(car, map);

		assertEquals("my id", map.getString(Token.of("id")));
	}

	@Test
	public void happy_columnizeCleanObject() {
		Car car = new Car();
		car.setId("my id");
		ObjectMap map = new ObjectMap();
		mappingHelper.columnize(car, map);

		assertEquals("my id", map.getString(Token.of("id")));
	}

	@Test
	public void happy_hydrate() {
		Car car = helper.factory.get(Car.class);

		ObjectMap map = new ObjectMap();
		map.put(Token.of("id"), "my id");
		map.put(Token.of("the","boolean"), true);
		mappingHelper.hydrate(car, map);

		assertEquals("my id", car.getId());
	}

	@Test
	public void happy_hydrateCleanObject() {
		Car car = new Car();

		ObjectMap map = new ObjectMap();
		map.put(Token.of("id"), "my id");
		map.put(Token.of("the","boolean"), true);
		mappingHelper.hydrate(car, map);

		assertEquals("my id", car.getId());
	}
}