package io.ran;

import io.ran.token.Token;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeySet {
	private boolean primary;
	private String name;
	TreeSet<Field> parts = new TreeSet<>(Comparator.comparing(Field::getOrder));

	public KeySet(List<Field> fields) {
		fields.forEach(this::add);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeySet keySet = (KeySet) o;
		return primary == keySet.primary && name.equals(keySet.name) && parts.equals(keySet.parts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(primary, name, parts);
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
		return add(property, null, parts.size());
	}

	public void forEach(Consumer<Field> action) {
		parts.forEach(action);
	}

	public KeySet add(Property<?> property, int order) {
		return add(property, null, order);
	}

	public KeySet add(Property<?> property, String name, int order) {
		if (this.name != null && name != null && !this.name.equals(name)) {
			throw new RuntimeException("Adding a key part with name: "+name+" to a key with name: "+this.name+" is invalid");
		}
		if (name != null) {
			this.name = name;
		}

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
		add(keyInfo.getProperty(), keyInfo.getName(), keyInfo.order());
		return this;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public  boolean isPrimary() {
		return primary;
	}

	public boolean matchesKeys(KeySet o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeySet keySet = (KeySet) o;
		if (parts.size() != keySet.parts.size()) return false;
		Iterator<Field> itt1 = parts.iterator();
		Iterator<Field> itt2 = keySet.parts.iterator();
		while(itt1.hasNext()) {
			if (!itt1.next().equals(itt2.next())) return false;
		}
		return true;
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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Field field = (Field) o;
			return order == field.order && property.equals(field.property);
		}

		@Override
		public int hashCode() {
			return Objects.hash(property, order);
		}
	}
}
