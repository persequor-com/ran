package io.ran.testclasses;

import io.ran.*;

import javax.inject.Inject;

public class IsItsOwnKeyRepository extends CrudRepositoryTestDoubleBase<IsItsOwnKey, IsItsOwnKey> {
	@Inject
	public IsItsOwnKeyRepository(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store, TypeDescriberFactory typeDescriberFactory) {
		super(genericFactory, IsItsOwnKey.class, IsItsOwnKey.class, mappingHelper, store, typeDescriberFactory);
	}
}
