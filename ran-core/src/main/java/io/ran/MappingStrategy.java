/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran;

public interface MappingStrategy {
	String getFieldName(String dbColumn);

	String getDbColumn(String fieldName);
}
