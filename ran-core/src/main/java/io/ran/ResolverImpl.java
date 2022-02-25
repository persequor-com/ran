package io.ran;

import javax.inject.Inject;
import java.util.Collection;

public class ResolverImpl implements Resolver {
	private final GenericFactory genericFactory;
	private final AutoMapper autoMapper;

	@Inject
	public ResolverImpl(GenericFactory genericFactory, AutoMapper autoMapper) {
		this.genericFactory = genericFactory;
		this.autoMapper = autoMapper;
	}

	private <FROM> RelationDescriber getRelationDescriber(Class<FROM> fromClass, String field) {
		TypeDescriber<FROM> typeDescriber = TypeDescriberImpl.getTypeDescriber(fromClass, autoMapper);
		return typeDescriber.relations().get(field);
	}

	private DbResolver<DbType> getDbResolver(RelationDescriber relationDescriber) {
		return genericFactory.getResolver(TypeDescriberImpl.getTypeDescriber(relationDescriber.getToClass().clazz, autoMapper).annotations().get(Mapper.class).dbType());
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
