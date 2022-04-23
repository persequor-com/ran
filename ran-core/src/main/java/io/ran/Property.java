package io.ran;

import io.ran.token.Token;

import java.util.*;
import java.util.stream.Collectors;

public class Property<T> {
	private Token token;
	private String snakeCase;
	private Clazz<T> type;
	private Clazz<?> on;
	private List<KeyInfo> keys  = new ArrayList<>();
	private Annotations annotations = new Annotations();

	private Property() {}

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
			collection.forEach(property -> propertyMap.put(property.getSnakeCase(), property));

			return super.addAll(i, collection);
		}

		@Override
		public boolean addAll(Collection<? extends Property> collection) {
			collection.forEach(property -> propertyMap.put(property.getSnakeCase(), property));
			return super.addAll(collection);
		}


		@Override
		public void add(int i, Property property) {
			propertyMap.put(property.getSnakeCase(), property);

			super.add(i, property);
		}

		@Override
		public boolean add(Property property) {
			propertyMap.put(property.getSnakeCase(), property);
			return super.add(property);
		}

		public void add(String snakeCase, Clazz<?> type) {
			Property<?> property = Property.get(Token.snake_case(snakeCase), type);
			add(property);
			propertyMap.put(property.getSnakeCase(), property);
		}

		public void add(Token token, Clazz<?> type) {
			Property<?> property = Property.get(token, type);
			add(property);
			propertyMap.put(property.getSnakeCase(), property);
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

			for(Property<?> property : this) {
				for(KeyInfo keyInfo : property.getKeys()) {
					keys
						.computeIfAbsent(keyInfo.getMapKey(), k -> KeySet.get())
						.add(keyInfo);
				}
			}
			if (keys.isEmpty() && contains(Token.of("id"))) {
				KeyInfo keyInfo = new KeyInfo(true, get("id"),"", 0, true);
				keys.put(keyInfo.getMapKey(), KeySet.get().add(keyInfo));
			}
			return keys;
		}

		public KeySet mapProperties(PropertyList other) {
			KeySet keySet = KeySet.get();

			for(Property otherProperty : other) {
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

	public static class PropertyValueList<T> extends ArrayList<PropertyValue<T>> {
		private Property<T> property;
		public PropertyValueList() {
		}

		public PropertyValueList(Property<T> property, Collection<T> list) {
			super(list.stream().map(property::value).collect(Collectors.toList()));
			this.property = property;
		}

		public PropertyValue get(Token token) {
			return stream().filter(pv -> pv.getProperty().getToken().equals(token)).map(pv -> (PropertyValue)pv).findFirst().get();
		}

		public Property<T> getProperty() {
			return property;
		}
	}
}
