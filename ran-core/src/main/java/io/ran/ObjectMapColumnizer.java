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
