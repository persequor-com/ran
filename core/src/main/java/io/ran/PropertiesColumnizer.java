package io.ran;

import io.ran.token.Token;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PropertiesColumnizer implements ObjectMapColumnizer {
	private Property.PropertyList properties;
	private List<Property.PropertyValue> values = new ArrayList<>();

	public PropertiesColumnizer(Property.PropertyList properties) {
		this.properties = properties;
	}

	public List<Property.PropertyValue> getValues() {
		return values;
	}

	private <T> void setInternal(Token key, T value) {
		if(properties.contains(key)) {
			values.add(((Property<T>)properties.get(key)).value((T)value));
		}
	}

	@Override
	public void set(Token key, String value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Character value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, ZonedDateTime value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, LocalDateTime value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, LocalDate value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Integer value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Short value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Long value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, UUID value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Double value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, BigDecimal value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Float value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Boolean value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Byte value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Enum<?> value) {
		setInternal(key, value);
	}

	@Override
	public void set(Token key, Collection<?> value) {
		setInternal(key, value);
	}
}
