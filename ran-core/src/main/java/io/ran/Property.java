/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.token.Token;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Property<T> {
	private Token token;
	private String snakeCase;
	private Clazz<T> type;
	private Clazz<?> on;
	private List<KeyInfo> keys = new ArrayList<>();
	private Annotations annotations = new Annotations();

	private Property() {
	}

	public static <T> Property<T> get() {
		return new Property<>();
	}

	public static <T> Property<T> get(Token token) {
		Property<T> field = new Property<>();
		field.token = token;
		field.snakeCase = token.snake_case();
		return field;
	}

	public static <T> Property<T> get(Token token, Clazz<T> type) {
		Property<T> field = new Property<>();
		field.token = token;
		field.type = type;
		field.snakeCase = token.snake_case();
		return field;
	}

	public static <T> Property<T> get(String snakeCaseToken, Clazz<T> type) {
		Property<T> field = new Property<>();
		field.token = Token.snake_case(snakeCaseToken);
		field.type = type;
		field.snakeCase = snakeCaseToken;
		return field;
	}

	public Token getToken() {
		return token;
	}

	public Clazz<T> getType() {
		return type;
	}

	public PropertyValue<T> value(T value) {
		return new PropertyValue<T>(this.copy(), value);
	}

	public PropertyValueList<T> values(T... values) {
		return new PropertyValueList<T>(this.copy(), Arrays.asList(values));
	}

	public PropertyValueList<T> values(Collection<T> values) {
		return new PropertyValueList<T>(this.copy(), values);
	}

	public static PropertyList list() {
		return new PropertyList();
	}

	public static PropertyList list(Property... properties) {
		return new PropertyList(Arrays.asList(properties));
	}

	public void addKey(KeyInfo key) {
		this.keys.add(key);
	}

	public List<KeyInfo> getKeys() {
		return keys;
	}

	public Clazz<?> getOn() {
		return on;
	}

	public Property<T> setOn(Clazz<?> on) {
		this.on = on;
		return this;
	}

	public boolean matchesSnakeCase(String snakeCase) {
		return this.snakeCase.equals(snakeCase);
	}

	public String getSnakeCase() {
		return snakeCase;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Property<?> property = (Property<?>) o;

		if (!Objects.equals(token, property.token)) return false;
		return Objects.equals(type, property.type);
	}

	@Override
	public int hashCode() {
		int result = token != null ? token.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Property<T> copy() {
		Property property = Property.get(token, type);
		property.snakeCase = snakeCase;
		property.on = on;
		property.annotations = annotations;
		property.keys = new ArrayList<>(keys);
		return property;
	}

	public Annotations getAnnotations() {
		return annotations;
	}

	public static class PropertyList extends ArrayList<Property> {
		private Map<String, Property> propertyMap = Collections.synchronizedMap(new HashMap<>());

		public PropertyList(List<Property> properties) {
			addAll(properties);
		}

		public PropertyList() {

		}


		@Override
		public boolean addAll(int i, Collection<? extends Property> collection) {
			throw new RuntimeException("Adding at position is unsupported by PropertyList");
		}

		@Override
		public boolean addAll(Collection<? extends Property> collection) {
			boolean changed = false;
			for (Property property : collection) {
				if (propertyMap.put(property.getSnakeCase(), property) == null) {
					super.add(property);
					changed = true;
				}
			}
			return changed;
		}


		@Override
		public void add(int i, Property property) {
			throw new RuntimeException("Adding at position is unsupported by PropertyList");
		}

		@Override
		public boolean add(Property property) {
			if (propertyMap.put(property.getSnakeCase(), property) == null) {
				return super.add(property);
			}
			return false;
		}

		public void add(String snakeCase, Clazz<?> type) {
			Property<?> property = Property.get(Token.snake_case(snakeCase), type);
			add(property);
		}

		public void add(Token token, Clazz<?> type) {
			Property<?> property = Property.get(token, type);
			add(property);
		}

		public boolean contains(Token token) {
			return propertyMap.containsKey(token.snake_case());
		}

		public boolean contains(String snakeCase) {
			return propertyMap.containsKey(snakeCase);
		}

		public PropertyList remove(PropertyList properties) {
			return stream().filter(p -> !properties.contains(p.getToken())).collect(Collectors.toCollection(PropertyList::new));
		}

		public KeySets keys() {
			KeySets keys = new KeySets();

			for (Property<?> property : this) {
				for (KeyInfo keyInfo : property.getKeys()) {
					keys
							.computeIfAbsent(keyInfo.getMapKey(), k -> KeySet.get())
							.add(keyInfo);
				}
			}
			if (keys.isEmpty() && contains(Token.of("id"))) {
				KeyInfo keyInfo = new KeyInfo(true, get("id"), "", 0, true);
				keys.put(keyInfo.getMapKey(), KeySet.get().add(keyInfo));
			}
			return keys;
		}

		public KeySet mapProperties(PropertyList other) {
			KeySet keySet = KeySet.get();

			for (Property otherProperty : other) {
				try {
					Clazz<?> otherType = otherProperty.getOn();
					Optional<Property> ownProperty = getOptional(Token.CamelCase(otherType.clazz.getSimpleName() + "Id"));
					KeySets keys = keys();
					if (otherProperty.matchesSnakeCase("id") && ownProperty.isPresent()) {
						keySet.parts.clear();
						keySet.add(ownProperty.get());
						return keySet;
					} else if (otherProperty.getToken().equals(Token.CamelCase(get(0).getOn().clazz.getSimpleName() + "Id")) && keys.getPrimary().size() == 1) {
						keySet.parts.clear();
						keySet.add(keys.getPrimary());
						return keySet;
					}
				} catch (NullPointerException exception) {
					throw new RuntimeException("unable to automatically map relation from " + otherProperty.getOn().clazz.getName() + " to " + get(0).getOn().clazz.getName(), exception);
				}
			}
//			if (keySet.isEmpty()) {
//				throw new RuntimeException("unable to automatically map relation from " + get(0).getOn().clazz.getName() + " to " + other.get(0).getOn().clazz.getName());
//			}
			return keySet;
		}

		public Optional<Property> getOptional(Token token) {
			return Optional.ofNullable(propertyMap.get(token.snake_case()));
		}

		public Optional<Property> getOptional(String snakeCase) {
			return Optional.ofNullable(propertyMap.get(snakeCase));
		}

		public Property<?> get(Token token) {
			try {
				return getOptional(token).orElseThrow(() -> new RuntimeException("Could not find property by token: " + token.toString() + " on " + stream().findAny().map(p -> p.getOn().clazz.getName()).orElse("unknown class")));
			} catch (Exception e) {
				throw e;
			}
		}

		public Property<?> get(String snakeCase) {
			try {
				return getOptional(snakeCase).orElseThrow(() -> new RuntimeException("Could not find property by snake_case: " + snakeCase + " on " + stream().findAny().map(p -> p.getOn().clazz.getName()).orElse("unknown class")));
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private boolean isKey() {
		return !keys.isEmpty();
	}

	private boolean isPrimaryKey() {
		return keys.stream().anyMatch(ki -> ki.isPrimary());
	}

	public static class PropertyValue<T> {
		private final Property<T> property;
		private T value;

		public PropertyValue(Property<T> property, T value) {
			this.property = property;
			this.value = value;
		}

		public Property<T> getProperty() {
			return property;
		}

		public T getValue() {
			return value;
		}

		public static <V extends Number> V getNumericValue(V value) {
			return getNumberValue(value).value();
		}

		public static <V extends Number, V2 extends Number> V2 getNumericValue(Class<V2> ofType, V value) {
			return getNumberValue(ofType, value).value();
		}

		private static <V extends Number> NumberValue<V> getNumberValue(V value) {
			return NumberValue.get(value);
		}

		private static <V extends Number> NumberValue<V> getNumberValue(Class<V> ofType,Object value) {
			return NumberValue.get(ofType, value);
		}

		public PropertyValue<T> add(PropertyValue<T> other) {
			return new PropertyValue<T>(getProperty(), (T) getNumberValue((Number)getValue()).add(getNumberValue((Number)other.getValue())).value());
		}

		public PropertyValue<T> subtract(PropertyValue<T> other) {
			return new PropertyValue<T>(getProperty(), (T) getNumberValue((Number)getValue()).subtract(getNumberValue((Number)other.getValue())).value());
		}

		public PropertyValue<T> multiply(PropertyValue<T> other) {
			return new PropertyValue<T>(getProperty(), (T) getNumberValue((Number)getValue()).multiply(getNumberValue((Number)other.getValue())).value());
		}

		public PropertyValue<T> divide(PropertyValue<T> other) {
			return new PropertyValue<T>(getProperty(), (T) getNumberValue((Number)getValue()).divide(getNumberValue((Number)other.getValue())).value());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			PropertyValue<?> that = (PropertyValue<?>) o;

			if (!Objects.equals(property, that.property)) return false;
			return Objects.equals(value, that.value);
		}

		@Override
		public int hashCode() {
			int result = property != null ? property.hashCode() : 0;
			result = 31 * result + (value != null ? value.hashCode() : 0);
			return result;
		}
	}

	public Clazz getClazz() {
		return type;
	}

	public static class PropertyValueList<T> extends ArrayList<PropertyValue<T>> {
		private Property<T> property;

		public PropertyValueList() {
		}

		public PropertyValueList(Property<T> property, Collection<T> list) {
			super(list.stream().map(property::value).collect(Collectors.toList()));
			this.property = property;
		}

		public PropertyValue get(Token token) {
			return stream().filter(pv -> pv.getProperty().getToken().equals(token)).map(pv -> (PropertyValue) pv).findFirst().get();
		}

		public Property<T> getProperty() {
			return property;
		}
	}

	public abstract static class NumberValue<V extends Number> {
		private static Map<Class, Function<Object, NumberValue>> types = new HashMap<>();
		static {
			types.put(Byte.class, ByteNumberValue::new);
			types.put(byte.class, ByteNumberValue::new);
			types.put(Short.class, ShortNumberValue::new);
			types.put(short.class, ShortNumberValue::new);
			types.put(Integer.class, IntegerNumberValue::new);
			types.put(int.class, IntegerNumberValue::new);
			types.put(Long.class, LongNumberValue::new);
			types.put(long.class, LongNumberValue::new);
			types.put(Float.class, FloatNumberValue::new);
			types.put(float.class, FloatNumberValue::new);
			types.put(Double.class, DoubleNumberValue::new);
			types.put(double.class, DoubleNumberValue::new);
			types.put(BigDecimal.class, BigDecimalNumberValue::new);
		}
		private final Object value;

		public static <V extends Number, NV extends NumberValue<V>> NV get(V value) {
			if (!types.containsKey(value.getClass())) {
				return null;
			}
			return (NV) types.get(value.getClass()).apply(value);
		}

		public static <V extends Number, NV extends NumberValue<V>> NV get(Class<V> ofType, Object value) {
			if (!types.containsKey(value.getClass())) {
				return null;
			}
			return (NV) types.get(ofType).apply(value);
		}

		private NumberValue(Object value) {
			this.value = value;
		}

		public V value() {
			return (V) value;
		}
		public abstract NumberValue<V> add(NumberValue<?> other);

		public abstract NumberValue<V> subtract(NumberValue<?> other);

		public abstract NumberValue<V> multiply(NumberValue<?> numberValue);

		public abstract NumberValue<V> divide(NumberValue<?> numberValue);

		private static class ByteNumberValue extends NumberValue<Byte> {
			public ByteNumberValue(Object value) {
				super(((Number)value).byteValue());
			}

			@Override
			public NumberValue<Byte> add(NumberValue<?> other) {
				return new ByteNumberValue(value() + PropertyValue.getNumberValue(Byte.class, other.value()).value());
			}

			@Override
			public NumberValue<Byte> subtract(NumberValue<?> other) {
				return new ByteNumberValue(value() - PropertyValue.getNumberValue(Byte.class, other.value()).value());
			}

			@Override
			public NumberValue<Byte> multiply(NumberValue<?> other) {
				return new ByteNumberValue(value() * PropertyValue.getNumberValue(Byte.class, other.value()).value());
			}
			@Override
			public NumberValue<Byte> divide(NumberValue<?> other) {
				return new ByteNumberValue(value() / PropertyValue.getNumberValue(Byte.class, other.value()).value());
			}
		}

		private static class ShortNumberValue extends NumberValue<Short> {
			public ShortNumberValue(Object value) {
				super(((Number)value).shortValue());
			}

			@Override
			public NumberValue<Short> add(NumberValue<?> other) {
				return new ShortNumberValue(value() + PropertyValue.getNumberValue(Short.class, other.value()).value());
			}

			@Override
			public NumberValue<Short> subtract(NumberValue<?> other) {
				return new ShortNumberValue(value() - PropertyValue.getNumberValue(Short.class, other.value()).value());
			}

			@Override
			public NumberValue<Short> multiply(NumberValue<?> other) {
				return new ShortNumberValue(value() * PropertyValue.getNumberValue(Short.class, other.value()).value());
			}

			@Override
			public NumberValue<Short> divide(NumberValue<?> other) {
				return new ShortNumberValue(value() / PropertyValue.getNumberValue(Short.class, other.value()).value());
			}
		}

		private static class IntegerNumberValue extends NumberValue<Integer> {
			public IntegerNumberValue(Object value) {
				super(((Number)value).intValue());
			}

			@Override
			public NumberValue<Integer> add(NumberValue<?> other) {
				return new IntegerNumberValue(value() + PropertyValue.getNumberValue(Integer.class, other.value()).value());
			}

			@Override
			public NumberValue<Integer> subtract(NumberValue<?> other) {
				return new IntegerNumberValue(value() - PropertyValue.getNumberValue(Integer.class, other.value()).value());
			}

			@Override
			public NumberValue<Integer> multiply(NumberValue<?> other) {
				return new IntegerNumberValue(value() * PropertyValue.getNumberValue(Integer.class, other.value()).value());
			}

			@Override
			public NumberValue<Integer> divide(NumberValue<?> other) {
				return new IntegerNumberValue(value() / PropertyValue.getNumberValue(Integer.class, other.value()).value());
			}
		}

		private static class LongNumberValue extends NumberValue<Long> {
			public LongNumberValue(Object value) {
				super(((Number)value).longValue());
			}

			@Override
			public NumberValue<Long> add(NumberValue<?> other) {
				return new LongNumberValue(value() + PropertyValue.getNumberValue(Long.class, other.value()).value());
			}

			@Override
			public NumberValue<Long> subtract(NumberValue<?> other) {
				return new LongNumberValue(value() - PropertyValue.getNumberValue(Long.class, other.value()).value());
			}

			@Override
			public NumberValue<Long> multiply(NumberValue<?> other) {
				return new LongNumberValue(value() * PropertyValue.getNumberValue(Long.class, other.value()).value());
			}

			@Override
			public NumberValue<Long> divide(NumberValue<?> other) {
				return new LongNumberValue(value() / PropertyValue.getNumberValue(Long.class, other.value()).value());
			}
		}

		private static class FloatNumberValue extends NumberValue<Float> {
			public FloatNumberValue(Object value) {
				super(((Number)value).floatValue());
			}

			@Override
			public NumberValue<Float> add(NumberValue<?> other) {
				return new FloatNumberValue(value() + PropertyValue.getNumberValue(Float.class, other.value()).value());
			}

			@Override
			public NumberValue<Float> subtract(NumberValue<?> other) {
				return new FloatNumberValue(value() - PropertyValue.getNumberValue(Float.class, other.value()).value());
			}

			@Override
			public NumberValue<Float> multiply(NumberValue<?> other) {
				return new FloatNumberValue(value() * PropertyValue.getNumberValue(Float.class, other.value()).value());
			}

			@Override
			public NumberValue<Float> divide(NumberValue<?> other) {
				return new FloatNumberValue(value() / PropertyValue.getNumberValue(Float.class, other.value()).value());
			}
		}

		private static class DoubleNumberValue extends NumberValue<Double> {
			public DoubleNumberValue(Object value) {
				super(((Number)value).doubleValue());
			}

			@Override
			public NumberValue<Double> add(NumberValue<?> other) {
				return new DoubleNumberValue(value() + PropertyValue.getNumberValue(Double.class, other.value()).value());
			}

			@Override
			public NumberValue<Double> subtract(NumberValue<?> other) {
				return new DoubleNumberValue(value() - PropertyValue.getNumberValue(Double.class, other.value()).value());
			}

			@Override
			public NumberValue<Double> multiply(NumberValue<?> other) {
				return new DoubleNumberValue(value() * PropertyValue.getNumberValue(Double.class, other.value()).value());
			}

			@Override
			public NumberValue<Double> divide(NumberValue<?> other) {
				return new DoubleNumberValue(value() / PropertyValue.getNumberValue(Double.class, other.value()).value());
			}
		}

		private static class BigDecimalNumberValue extends NumberValue<BigDecimal> {
			public BigDecimalNumberValue(Object value) {
				super(value instanceof BigDecimal ? value : new BigDecimal(String.valueOf(value)));
			}

			@Override
			public NumberValue<BigDecimal> add(NumberValue<?> other) {
				return new BigDecimalNumberValue(value().add(PropertyValue.getNumberValue(BigDecimal.class, other.value()).value()));
			}

			@Override
			public NumberValue<BigDecimal> subtract(NumberValue<?> other) {
				return new BigDecimalNumberValue(value().subtract(PropertyValue.getNumberValue(BigDecimal.class, other.value()).value()));
			}

			@Override
			public NumberValue<BigDecimal> multiply(NumberValue<?> other) {
				return new BigDecimalNumberValue(value().multiply(PropertyValue.getNumberValue(BigDecimal.class, other.value()).value()));
			}

			@Override
			public NumberValue<BigDecimal> divide(NumberValue<?> other) {
				return new BigDecimalNumberValue(value().divide(PropertyValue.getNumberValue(BigDecimal.class, other.value()).value()));
			}
		}
	}
}
