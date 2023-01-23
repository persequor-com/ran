/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-04-01
 */
package io.ran;

public interface ISerializer {
	<T> T deserialize(Class<T> clazz, String value);

	<T> String serialize(T object);
}
