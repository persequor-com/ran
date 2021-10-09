/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import io.ran.token.Token;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

public interface ObjectMapColumnizer {
	void set(Token key, String value);
	void set(Token key, Character value);
	void set(Token key, ZonedDateTime value);
	void set(Token key, LocalDateTime value);
	void set(Token key, Integer value);
	void set(Token key, Short value);
	void set(Token key, Long value);
	void set(Token key, UUID value);
	void set(Token key, Double value);
	void set(Token key, BigDecimal value);
	void set(Token key, Float value);
	void set(Token key, Boolean value);
	void set(Token key, Byte value);
	void set(Token key, Enum<?> value);
	void set(Token key, Collection<?> value);
}
