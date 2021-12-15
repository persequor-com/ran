/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import io.ran.ObjectMapColumnizer;
import io.ran.ObjectMapHydrator;
import io.ran.token.Token;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ObjectMap extends HashMap<Token, Object> implements ObjectMapHydrator, ObjectMapColumnizer {

	@Override
	public void set(Token key, String value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Character value) {
		put(key, value);
	}

	@Override
	public void set(Token key, ZonedDateTime value) {
		put(key, value);
	}

	@Override
	public void set(Token key, LocalDateTime value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Instant value) {
		put(key, value);
	}

	@Override
	public void set(Token key, LocalDate value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Integer value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Short value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Long value) {
		put(key, value);
	}

	@Override
	public void set(Token key, UUID value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Double value) {
		put(key, value);
	}

	@Override
	public void set(Token key, BigDecimal value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Float value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Boolean value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Byte value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Enum<?> value) {
		put(key, value);
	}

	@Override
	public void set(Token key, Collection<?> value) {
		put(key, value);
	}

	@Override
	public String getString(Token key) {
		return (String) get(key);
	}

	@Override
	public Character getCharacter(Token key) {
		return (Character)get(key);
	}

	@Override
	public ZonedDateTime getZonedDateTime(Token key) {
		return (ZonedDateTime) get(key);
	}

	@Override
	public Instant getInstant(Token key) {
		return (Instant) get(key);
	}

	@Override
	public LocalDateTime getLocalDateTime(Token key) {
		return (LocalDateTime) get(key);
	}

	@Override
	public LocalDate getLocalDate(Token key) {
		return (LocalDate) get(key);
	}

	@Override
	public Integer getInteger(Token key) {
		return (Integer) get(key);
	}

	@Override
	public Short getShort(Token key) {
		return (Short)get(key);
	}

	@Override
	public Long getLong(Token key) {
		return (Long) get(key);
	}

	@Override
	public UUID getUUID(Token key) {
		return (UUID) get(key);
	}

	@Override
	public Double getDouble(Token key) {
		return (Double) get(key);
	}

	@Override
	public BigDecimal getBigDecimal(Token key) {
		return (BigDecimal) get(key);
	}

	@Override
	public Float getFloat(Token key) {
		return (Float) get(key);
	}

	@Override
	public Boolean getBoolean(Token key) {
		return (Boolean)get(key);
	}

	@Override
	public Byte getByte(Token key) {
		return (Byte)get(key);
	}

	@Override
	public <T extends Enum<T>> T getEnum(Token key, Class<T> enumType) {
		return (T)get(key);
	}

	@Override
	public <T> Collection<T> getCollection(Token key, Class<T> elementType, Class<? extends Collection<T>> collectionClass) {
		return (Collection<T>)get(key);
	}


	public void set(String key, String value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, ZonedDateTime value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, Instant value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, Integer value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, Long value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, UUID value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, Double value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, BigDecimal value) {
		put(Token.CamelCase(key), value);
	}

	public void set(String key, Float value) {
		put(Token.CamelCase(key), value);
	}

	public String getString(String key) {
		return (String) get(Token.CamelCase(key));
	}

	public ZonedDateTime getZonedDateTime(String key) {
		return (ZonedDateTime) get(Token.CamelCase(key));
	}

	public Instant getInstant(String key) {
		return (Instant) get(Token.CamelCase(key));
	}

	public Integer getInteger(String key) {
		return (Integer) get(Token.CamelCase(key));
	}

	public Long getLong(String key) {
		return (Long) get(Token.CamelCase(key));
	}

	public UUID getUUID(String key) {
		return (UUID) get(Token.CamelCase(key));
	}

	public Double getDouble(String key) {
		return (Double) get(Token.CamelCase(key));
	}

	public BigDecimal getBigDecimal(String key) {
		return (BigDecimal) get(Token.CamelCase(key));
	}

	public Float getFloat(String key) {
		return (Float) get(Token.CamelCase(key));
	}
}
