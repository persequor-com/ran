/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
