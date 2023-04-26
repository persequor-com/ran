/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import javax.inject.Inject;
import java.util.Collection;

public class ResolverImpl implements Resolver {
	private GenericFactory genericFactory;

	@Inject
	public ResolverImpl(GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	private <FROM> RelationDescriber getRelationDescriber(Class<FROM> fromClass, String field) {
		TypeDescriber<FROM> typeDescriber = TypeDescriberImpl.getTypeDescriber(fromClass);
		return typeDescriber.relations().get(field);
	}

	private DbResolver<DbType> getDbResolver(RelationDescriber relationDescriber) {
		Mapper mapperAnnotation = TypeDescriberImpl.getTypeDescriber(relationDescriber.getToClass().clazz).annotations().get(Mapper.class);
		if (mapperAnnotation == null) {
			throw new MissingDbTypeException("Unable to find db type from @Mapper annotation on target class: " + relationDescriber.getToClass().clazz.getName());
		}

		return genericFactory.getResolver(mapperAnnotation.dbType());
	}

	@Override
	public <FROM, TO> TO get(Class<FROM> fromClass, String field, FROM obj) {
		RelationDescriber relationDescriber = getRelationDescriber(fromClass, field);
		return getDbResolver(relationDescriber).get(relationDescriber, obj);
	}

	@Override
	public <FROM, TO> Collection<TO> getCollection(Class<FROM> fromClass, String field, FROM obj) {
		RelationDescriber relationDescriber = getRelationDescriber(fromClass, field);
		return getDbResolver(relationDescriber).getCollection(relationDescriber, obj);
	}
}
