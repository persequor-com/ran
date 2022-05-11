/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import io.ran.token.Token;

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
	void _setRelationNotLoaded(RelationDescriber relationDescriber);
	Object _getRelation(RelationDescriber relationDescriber);
	Object _getRelation(Token token);
	Object _getRelation(Object object, RelationDescriber relationDescriber);
	Object _getRelation(Object object, Token token);
	boolean _isChanged();
	TypeDescriber _getDescriber();
    void copy(Object from, Object to);
}
