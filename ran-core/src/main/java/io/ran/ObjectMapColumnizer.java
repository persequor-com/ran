/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import io.ran.token.Token;

import java.math.BigDecimal;
import java.time.*;
import java.util.Collection;
import java.util.UUID;

public interface ObjectMapColumnizer {
	void set(Property key, String value);
	void set(Property key, Character value);
	void set(Property key, ZonedDateTime value);
	void set(Property key, LocalDateTime value);
	void set(Property key, LocalTime value);
	void set(Property key, Instant value);
	void set(Property key, LocalDate value);
	void set(Property key, Integer value);
	void set(Property key, Short value);
	void set(Property key, Long value);
	void set(Property key, UUID value);
	void set(Property key, Double value);
	void set(Property key, BigDecimal value);
	void set(Property key, Float value);
	void set(Property key, Boolean value);
	void set(Property key, Byte value);
	void set(Property key, byte[] value);
	void set(Property key, Enum<?> value);
	void set(Property key, Collection<?> value);
}