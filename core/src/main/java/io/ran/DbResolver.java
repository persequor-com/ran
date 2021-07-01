/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import java.util.Collection;

public interface DbResolver<DbType> {
	<FROM, TO> TO get(RelationDescriber relationDescriber, FROM obj);
	<FROM, TO> Collection<TO> getCollection(RelationDescriber relationDescriber, FROM obj);
}
