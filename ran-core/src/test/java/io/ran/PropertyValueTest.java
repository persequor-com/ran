package io.ran;

import io.ran.token.Token;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class PropertyValueTest {
	private Property property = Property.get(Token.of("my"));

	@Test
	public void getNumericValue_simple_int() {
		Object actual = property.value(4).add(property.value(3)).getValue();
		assertEquals(7, actual);
		actual = property.value(new Integer(4)).add(property.value(new Integer(3))).getValue();
		assertEquals(7, actual);
		actual = property.value(4).subtract(property.value(3)).getValue();
		assertEquals(1, actual);
		actual = property.value(4).multiply(property.value(3)).getValue();
		assertEquals(12, actual);
		actual = property.value(new Integer(4)).multiply(property.value(new Integer(3))).getValue();
		assertEquals(12, actual);
		actual = property.value(new Integer(4)).divide(property.value(new Integer(2))).getValue();
		assertEquals(2, actual);
	}
	@Test
	public void getNumericValue_casts() {
		Primitives.getNumbers().forEach(number1 -> {
			Primitives.getNumbers().forEach(number2 -> {
				System.out.println(number1.getBoxed().getName());
				System.out.println(number2.getBoxed().getName());
				try {
					Object n1 = number1.getBoxed().getConstructor(number1.getUnboxed()).newInstance(Property.PropertyValue.getNumericValue(number1.getBoxed(),4));
					Object n2 = number2.getBoxed().getConstructor(number2.getUnboxed()).newInstance(Property.PropertyValue.getNumericValue(number2.getBoxed(),2));
					Object actual = property.value(n1).add(property.value(n2)).getValue();
					assertEquals((Integer) 6, Integer.valueOf(actual.toString().replace(".0","")));
					actual = property.value(n1).subtract(property.value(n2)).getValue();
					assertEquals((Integer) 2, Integer.valueOf(actual.toString().replace(".0","")));
					actual = property.value(n1).multiply(property.value(n2)).getValue();
					assertEquals((Integer) 8, Integer.valueOf(actual.toString().replace(".0","")));
					actual = property.value(n1).divide(property.value(n2)).getValue();
					assertEquals((Integer) 2, Integer.valueOf(actual.toString().replace(".0","")));
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
						 InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			});
		});
	}
}
