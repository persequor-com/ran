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
