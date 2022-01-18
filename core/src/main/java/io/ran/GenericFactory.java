/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

public interface GenericFactory {
	<T> T get(Class<T> clazz);
	<T> T getQueryInstance(Class<T> clazz);

	DbResolver<DbType> getResolver(Class<? extends DbType> dbTypeClass);

	<T> T wrapped(Class<T> aClass);
}
