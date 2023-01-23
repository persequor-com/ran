/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

	private <T> void setInternal(Property key, T value) {
		if (properties.contains(key)) {
			values.add(((Property<T>) properties.get(key.getToken())).value((T) value));
		}
	}

	@Override
	public void set(Property key, String value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Character value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, ZonedDateTime value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, LocalDateTime value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, LocalTime value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Instant value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, LocalDate value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Integer value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Short value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Long value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, UUID value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Double value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, BigDecimal value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Float value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Boolean value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Byte value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, byte[] value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Enum<?> value) {
		setInternal(key, value);
	}

	@Override
	public void set(Property key, Collection<?> value) {
		setInternal(key, value);
	}
}
