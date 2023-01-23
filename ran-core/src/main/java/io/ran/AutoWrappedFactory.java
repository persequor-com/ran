/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-12-12
 */
package io.ran;

public interface AutoWrappedFactory {
	<T> T get(DynamicClassIdentifier identifier);
}
