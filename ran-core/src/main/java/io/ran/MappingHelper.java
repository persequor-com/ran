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

import javax.inject.Inject;
import java.util.function.Consumer;
import java.util.function.Function;

public class MappingHelper {
	private GenericFactory genericFactory;

	@Inject
	public MappingHelper(GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	public void hydrate(Object toHydrate, ObjectMapHydrator hydrator) {
		if (toHydrate instanceof Mapping) {
			((Mapping) toHydrate).hydrate(hydrator);
		} else {
			((Mapping) genericFactory.get(toHydrate.getClass())).hydrate(toHydrate, hydrator);
		}
	}

	public void columnize(Object toColumnize, ObjectMapColumnizer columnizer) {
		if (toColumnize instanceof Mapping) {
			((Mapping) toColumnize).columnize(columnizer);
		} else {
			((Mapping) genericFactory.get(toColumnize.getClass())).columnize(toColumnize, columnizer);
		}
	}

	public CompoundKey getKey(Object obj) {
		if (obj instanceof Mapping) {
			return ((Mapping) obj)._getKey();
		} else {
			return ((Mapping) genericFactory.get(obj.getClass()))._getKey(obj);
		}
	}

	public Object getValue(Object obj, Property property) {
		return getValue(obj, property, null);
	}

	public Object getValue(Object obj, Property property, Object defaultValue) {
		Object value;
		if (obj instanceof Mapping) {
			value = ((Mapping) obj)._getValue(property);
		} else {
			value = ((Mapping) genericFactory.get(obj.getClass()))._getValue(obj, property);
		}
		if (value == null && !property.getType().isPrimitive()) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public void setValue(Object obj, Property property, Object value) {
		if (obj instanceof Mapping) {
			((Mapping) obj)._setValue(property, value);
		} else {
			((Mapping) genericFactory.get(obj.getClass()))._setValue(obj, property, value);
		}
	}

	public Object getRelation(Object obj, RelationDescriber describer) {
		if (obj instanceof Mapping) {
			return ((Mapping) obj)._getRelation(describer);
		} else {
			return ((Mapping) genericFactory.get(obj.getClass()))._getRelation(obj, describer);
		}
	}

	public Object getRelation(Object obj, Token fieldToken) {
		if (obj instanceof Mapping) {
			return ((Mapping) obj)._getRelation(fieldToken);
		} else {
			return ((Mapping) genericFactory.get(obj.getClass()))._getRelation(obj, fieldToken);
		}
	}

	public <T> ClazzMethod getMethod(Class<T> tClass, Function<T, ?> methodReference) {
		TypeDescriberImpl.getTypeDescriber(tClass);
		T queryInstance = genericFactory.getQueryInstance(tClass);
		methodReference.apply(queryInstance);
		return ((QueryWrapper) queryInstance).getCurrentMethod();
	}

	public <T> ClazzMethod getMethod(Class<T> tClass, Consumer<T> methodReference) {
		TypeDescriberImpl.getTypeDescriber(tClass);
		T queryInstance = genericFactory.getQueryInstance(tClass);
		methodReference.accept(queryInstance);
		return ((QueryWrapper) queryInstance).getCurrentMethod();
	}

	public <T> void copyValues(Class<T> tClass, T from, T to) {
		Mapping mapping = (Mapping) genericFactory.get(tClass);
		mapping.copy(from, to);
	}

	public <T> T makeCopy(Class<T> tClass, T t) {
		Mapping mapping = (Mapping) genericFactory.get(tClass);
		mapping.copy(t, mapping);
		return (T) mapping;
	}
}
