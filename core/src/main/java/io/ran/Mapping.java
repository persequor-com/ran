/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

public interface Mapping {
	Object _getValue(Property property);
	Object _getValue(Object obj, Property property);
	void hydrate(ObjectMapHydrator hydrator);
	void hydrate(Object obj, ObjectMapHydrator hydrator);
	void columnize(ObjectMapColumnizer columnizer);
	void columnize(Object obj, ObjectMapColumnizer columnizer);
	CompoundKey _getKey();
	CompoundKey _getKey(Object obj);
	void _setRelation(RelationDescriber relationDescriber, Object value);
	Object _getRelation(RelationDescriber relationDescriber);

	boolean _isChanged();
	TypeDescriber _getDescriber();
}
