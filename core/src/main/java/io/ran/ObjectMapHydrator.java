/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import io.ran.token.Token;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

public interface ObjectMapHydrator {
	String getString(Token key);
	Character getCharacter(Token key);
	ZonedDateTime getZonedDateTime(Token key);
	Instant getInstant(Token key);
	Integer getInteger(Token key);
	Short getShort(Token key);
	Long getLong(Token key);
	UUID getUUID(Token key);
	Double getDouble(Token key);
	BigDecimal getBigDecimal(Token key);
	Float getFloat(Token key);
	Boolean getBoolean(Token key);
	Byte getByte(Token key);
	<T extends Enum<T>> T getEnum(Token key, Class<T> enumType);
	<T> Collection<T> getCollection(Token key, Class<T> elementType, Class<? extends Collection<T>> collectionType);
}
