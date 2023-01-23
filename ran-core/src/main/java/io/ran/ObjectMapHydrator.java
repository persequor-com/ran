/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

public interface ObjectMapHydrator {
	String getString(Property key);

	Character getCharacter(Property key);

	ZonedDateTime getZonedDateTime(Property key);

	Instant getInstant(Property key);

	LocalDateTime getLocalDateTime(Property key);

	LocalDate getLocalDate(Property key);

	LocalTime getLocalTime(Property key);

	Integer getInteger(Property key);

	Short getShort(Property key);

	Long getLong(Property key);

	UUID getUUID(Property key);

	Double getDouble(Property key);

	BigDecimal getBigDecimal(Property key);

	Float getFloat(Property key);

	Boolean getBoolean(Property key);

	Byte getByte(Property key);

	byte[] getBytes(Property key);

	<T extends Enum<T>> T getEnum(Property key, Class<T> enumType);

	<T> Collection<T> getCollection(Property key, Class<T> elementType, Class<? extends Collection<T>> collectionType);
}
