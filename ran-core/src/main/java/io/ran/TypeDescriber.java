/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import io.ran.token.Token;

import java.util.List;

public interface TypeDescriber<T> {
	KeySet primaryKeys();

	List<KeySet> indexes();

	Property.PropertyList fields();

	Property.PropertyList allFields();

	List<ClazzMethod> methods();

	RelationDescriber.RelationDescriberList relations();

	Class<T> clazz();

	Annotations annotations();

	Property getPropertyFromSnakeCase(String snakeCase);

	Token getTokenFromSnakeCase(String snakeCase);

}
