/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.token.Token;

public interface Mapping {
	Object _getValue(Property property);

	Object _getValue(Object obj, Property property);

	void _setValue(Property property, Object value);

	void _setValue(Object obj, Property property, Object value);

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
