/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

public class TestResolver implements DbResolver<TestDbType> {
	private GenericFactory genericFactory;
	private MappingHelper mappingHelper;

	@Inject
	public TestResolver(GenericFactory genericFactory, MappingHelper mappingHelper) {
		this.genericFactory = genericFactory;
		this.mappingHelper = mappingHelper;
	}

	@Override
	public <FROM, TO> TO get(RelationDescriber relationDescriber, FROM obj) {
		Clazz toClass = relationDescriber.getToClass();
		TO instance = (TO) genericFactory.get(toClass.clazz);
		return instance;
	}

	@Override
	public <FROM, TO> Collection<TO> getCollection(RelationDescriber relationDescriber, FROM obj) {
		Collection<TO> collection = new ArrayList<>();
		collection.add(get(relationDescriber, obj));
		collection.add(get(relationDescriber, obj));
		return collection;
	}
}
