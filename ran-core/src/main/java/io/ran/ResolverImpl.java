package io.ran;

import javax.inject.Inject;
import java.util.Collection;

public class ResolverImpl implements Resolver {
	private final GenericFactory genericFactory;
	private final TypeDescriberFactory typeDescriberFactory;

	@Inject
	public ResolverImpl(GenericFactory genericFactory, TypeDescriberFactory typeDescriberFactory) {
		this.genericFactory = genericFactory;
		this.typeDescriberFactory = typeDescriberFactory;
	}

	private <FROM> RelationDescriber getRelationDescriber(Class<FROM> fromClass, String field) {
		TypeDescriber<FROM> typeDescriber = typeDescriberFactory.getTypeDescriber(fromClass);
		return typeDescriber.relations().get(field);
	}

	private DbResolver<DbType> getDbResolver(RelationDescriber relationDescriber) {
		return genericFactory.getResolver(typeDescriberFactory.getTypeDescriber(relationDescriber.getToClass().clazz).annotations().get(Mapper.class).dbType());
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
