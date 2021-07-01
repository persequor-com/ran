package io.ran;

import io.ran.token.Token;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeySet {
	private boolean primary;
	TreeSet<Field> parts = new TreeSet<>(Comparator.comparing(Field::getOrder));

	public KeySet(List<Field> fields) {
		fields.forEach(this::add);
	}

	public static KeySet get(Field... fields) {
		return new KeySet(Arrays.stream(fields).collect(Collectors.toList()));
	}

	public Field get(Token token)  {
		return parts.stream().filter(f -> f.getToken().equals(token)).findFirst().orElseThrow(() -> new RuntimeException("Non existing field in key: "+token.toString()));
	}

	public KeySet add(KeySet keys) {
		parts.addAll(keys.parts);
		return this;
	}

	public KeySet add(Field field) {
		if(field.order == -1) {
			field.order = parts.size()-1;
		}
		parts.add(field);
		return this;
	}

	public KeySet add(Property<?> property) {
		return add(property, parts.size());
	}

	public void forEach(Consumer<Field> action) {
		parts.forEach(action);
	}

	public KeySet add(Property<?> property, int order) {
		Field field = new Field(property, order == -1 ? parts.size() : order);
		if (parts.contains(field)) {
			throw new RuntimeException(order+" position was already used in key. Ensure your key orders are unique.");
		}
		parts.add(field);
		return this;
	}

	public boolean isEmpty() {
		return parts.isEmpty();
	}

	public int size() {
		return parts.size();
	}

	public Field get(int i) {
		return parts.stream().skip(i).findFirst().orElseThrow(() -> new RuntimeException("Could not find  index"+i+" in key set"));
	}

	public Stream<Field> stream() {
		return parts.stream();
	}

	public Property.PropertyList toProperties() {
		return parts.stream().map(Field::getProperty).collect(Collectors.toCollection(Property.PropertyList::new));
	}

	public KeySet add(KeyInfo keyInfo) {
		if (keyInfo.isPrimary()) {
			primary = true;
		}
		add(keyInfo.getProperty(), keyInfo.order());
		return this;
	}

	public  boolean isPrimary() {
		return primary;
	}

	public static class Field {
		private Property<?> property;
		private int order;

		public Field(Property<?> property, int order) {
			this.property = property;
			this.order = order;
		}

		public Property<?> getProperty() {
			return property;
		}

		public int getOrder() {
			return order;
		}

		public Token getToken() {
			return property.getToken();
		}

		public Clazz<?> getType() {
			return property.getType();
		}

		public Clazz<?> getOn() {
			return property.getOn();
		}
	}
}
