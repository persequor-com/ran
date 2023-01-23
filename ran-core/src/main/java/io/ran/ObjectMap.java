/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran;

import io.ran.token.Token;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ObjectMap extends HashMap<Token, Object> implements ObjectMapHydrator, ObjectMapColumnizer {

	@Override
	public void set(Property key, String value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Character value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, ZonedDateTime value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, LocalDateTime value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, LocalTime value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Instant value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, LocalDate value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Integer value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Short value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Long value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, UUID value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Double value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, BigDecimal value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Float value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Boolean value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Byte value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, byte[] value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Enum<?> value) {
		put(key.getToken(), value);
	}

	@Override
	public void set(Property key, Collection<?> value) {
		put(key.getToken(), value);
	}

	@Override
	public String getString(Property key) {
		return (String) get(key.getToken());
	}

	@Override
	public Character getCharacter(Property key) {
		return (Character) get(key.getToken());
	}

	@Override
	public ZonedDateTime getZonedDateTime(Property key) {
		return (ZonedDateTime) get(key.getToken());
	}

	@Override
	public Instant getInstant(Property key) {
		return (Instant) get(key.getToken());
	}

	@Override
	public LocalDateTime getLocalDateTime(Property key) {
		return (LocalDateTime) get(key.getToken());
	}

	@Override
	public LocalDate getLocalDate(Property key) {
		return (LocalDate) get(key.getToken());
	}

	@Override
	public LocalTime getLocalTime(Property key) {
		return (LocalTime) get(key.getToken());
	}

	@Override
	public Integer getInteger(Property key) {
		return (Integer) get(key.getToken());
	}

	@Override
	public Short getShort(Property key) {
		return (Short) get(key.getToken());
	}

	@Override
	public Long getLong(Property key) {
		return (Long) get(key.getToken());
	}

	@Override
	public UUID getUUID(Property key) {
		return (UUID) get(key.getToken());
	}

	@Override
	public Double getDouble(Property key) {
		return (Double) get(key.getToken());
	}

	@Override
	public BigDecimal getBigDecimal(Property key) {
		return (BigDecimal) get(key.getToken());
	}

	@Override
	public Float getFloat(Property key) {
		return (Float) get(key.getToken());
	}

	@Override
	public Boolean getBoolean(Property key) {
		return (Boolean) get(key.getToken());
	}

	@Override
	public Byte getByte(Property key) {
		return (Byte) get(key.getToken());
	}

	@Override
	public byte[] getBytes(Property key) {
		return (byte[]) get(key.getToken());
	}

	@Override
	public <T extends Enum<T>> T getEnum(Property key, Class<T> enumType) {
		return (T) get(key.getToken());
	}

	@Override
	public <T> Collection<T> getCollection(Property key, Class<T> elementType, Class<? extends Collection<T>> collectionClass) {
		return (Collection<T>) get(key.getToken());
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
