/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2023-01-10
 */
package io.ran;

public interface AutoWrapperGenericFactory {
	<T> T wrapped(Class<T> aClass);
}
